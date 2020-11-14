package rganel.demo.service;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import rganel.demo.bo.MessageConsumerBo;
import rganel.demo.bo.MessageProducerBo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MessagesServlet extends HttpServlet {
    private final MessageConsumerBo messageConsumerBo;
    private final MessageProducerBo messageProducerBo;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String message = request.getParameter("message");
        String chatMessage = name + ": " + message;
        messageProducerBo.sendMessage(chatMessage);

        response.sendRedirect("/chat.html");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> messages = messageConsumerBo.getMessages();
        for (String message : messages) {
            response.getOutputStream().println("<p>" + message + "</p>");
        }
    }
}
