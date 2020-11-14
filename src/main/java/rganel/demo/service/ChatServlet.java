package rganel.demo.service;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rganel.demo.annotation.LoadResource;
import rganel.demo.annotation.ResourceArgument;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ChatServlet extends HttpServlet {
    public static String username = "";

    @Override
    @LoadResource(path = "chat.html")
    @ResourceArgument(key = "username", variable = "username")
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        username = request.getParameter("name");
    }
    
    @Override
    @LoadResource(path = "chat.html")
    @ResourceArgument(key = "username", variable = "username")
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
    }
}
