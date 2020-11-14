package rganel.demo.service;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rganel.demo.annotation.LoadResource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HomeServlet extends HttpServlet {
    private final ChatServlet chatServlet;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        chatServlet.username = request.getParameter("name");

        response.sendRedirect("/chat.html");
    }

    @LoadResource(path = "index.html")
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
    }
}
