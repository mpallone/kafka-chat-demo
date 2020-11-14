package rganel.demo.server;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import rganel.demo.bo.KafkaConsumerBo;
import rganel.demo.bo.KafkaProducerBo;
import rganel.demo.service.ChatServlet;
import rganel.demo.service.MessagesServlet;
import rganel.demo.service.HomeServlet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DemoServer {
    private final HomeServlet homeServlet;
    private final ChatServlet chatServlet;
    private final MessagesServlet messagesServlet;
    private final KafkaProducerBo kafkaProducerBo;
    private final KafkaConsumerBo kafkaConsumerBo;

    private void start() throws Exception {
        log.info("Starting server");
        Server server = new Server(8080);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");

        server.setHandler(contextHandler);

        contextHandler.addServlet(new ServletHolder(homeServlet), "/index.html");
        contextHandler.addServlet(new ServletHolder(chatServlet), "/chat.html");
        contextHandler.addServlet(new ServletHolder(messagesServlet), "/messages");

        ScheduledExecutorService kafkaConsumerExecutorService = Executors.newSingleThreadScheduledExecutor();
        kafkaConsumerExecutorService.scheduleAtFixedRate(kafkaConsumerBo, 0, 100, TimeUnit.MILLISECONDS);

        server.start();
        server.join();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop(server, kafkaConsumerExecutorService)));
    }

    private void stop(Server server, ScheduledExecutorService kafkaConsumerExecutorService) {
        log.info("Stopping server");
        kafkaProducerBo.stop();

        try {
            kafkaConsumerExecutorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            kafkaConsumerExecutorService.shutdownNow();
        }

        kafkaConsumerBo.stop();

        server.destroy();
    }

    public static void main(String[] args) {
        try {
            Injector injector = Guice.createInjector(new DemoModule());
            DemoServer demoServer = injector.getProvider(DemoServer.class).get();

            demoServer.start();
        } catch (Exception e) {
            log.error("Failed to start server", e);
            System.exit(1);
        }
    }
}
