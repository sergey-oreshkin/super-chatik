package home.serg.chatik.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.serg.chatik.context.LoginContext;
import home.serg.chatik.dto.LoginDto;
import home.serg.chatik.dto.TokenDto;
import home.serg.chatik.exception.AuthorizationException;
import home.serg.chatik.exception.ValidationException;
import home.serg.chatik.service.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final LoginService loginService = LoginContext.LOGIN_SERVICE.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (BufferedReader reader = req.getReader()) {
            String body = reader.lines().collect(Collectors.joining(" "));
            LoginDto loginDto = mapper.readValue(body, LoginDto.class);
            TokenDto tokenDto = loginService.login(loginDto);
            PrintWriter writer = resp.getWriter();
            resp.setStatus(200);
            resp.setContentType("application/json");
            writer.print(mapper.writeValueAsString(tokenDto));
            writer.flush();
        } catch (AuthorizationException ex) {
            resp.setStatus(403);
        } catch (ValidationException | JsonProcessingException ex){
            resp.setStatus(400);
        }
    }
}
