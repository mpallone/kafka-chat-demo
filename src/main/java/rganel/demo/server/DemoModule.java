package rganel.demo.server;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import rganel.demo.annotation.LoadResource;
import rganel.demo.annotation.LoadResourceInterceptor;
import rganel.demo.bo.KafkaConsumerBo;
import rganel.demo.bo.KafkaProducerBo;
import rganel.demo.bo.MessageConsumerBo;
import rganel.demo.bo.MessageProducerBo;
import rganel.demo.domain.KafkaConfig;

import javax.servlet.http.HttpServlet;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DemoModule extends AbstractModule {

    @Override
    public void configure() {
        bind(MessageConsumerBo.class).to(KafkaConsumerBo.class).in(Scopes.SINGLETON);
        bind(MessageProducerBo.class).to(KafkaProducerBo.class).in(Scopes.SINGLETON);

        bindInterceptor(Matchers.subclassesOf(HttpServlet.class), Matchers.annotatedWith(LoadResource.class), new LoadResourceInterceptor());
    }

    @Provides
    @Singleton
    KafkaConfig kafkaConfig() {
        return new KafkaConfig(System.getenv("KAFKA_TOPIC"), String.format("%s:%s", System.getenv("KAFKA_LOCATION"), System.getenv("KAFKA_PORT")));
    }

    @Provides
    @Singleton
    KafkaProducerBo kafkaProducerBo(KafkaConfig kafkaConfig) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getURI());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        Producer<String, String> producer = new KafkaProducer<>(props);

        return new KafkaProducerBo(producer, kafkaConfig);
    }

    @Provides
    @Singleton
    KafkaConsumerBo kafkaConsumerBo(KafkaConfig kafkaConfig) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getURI());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "message-consumers");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        Consumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(kafkaConfig.getTOPIC()));

        consumer.seekToBeginning(consumer.assignment());

        KafkaConsumerBo kafkaConsumerBo = new KafkaConsumerBo(consumer);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(kafkaConsumerBo, 0, 100, TimeUnit.MILLISECONDS);

        return kafkaConsumerBo;
    }
}
