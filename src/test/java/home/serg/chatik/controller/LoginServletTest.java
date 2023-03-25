package home.serg.chatik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.serg.chatik.TestUtil;
import home.serg.chatik.dto.LoginDto;
import home.serg.chatik.dto.TokenDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static home.serg.chatik.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServletTest {

    ObjectMapper mapper = new ObjectMapper();
    LoginServlet loginServlet = new LoginServlet();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeAll
    public static void initDatabase() {
        initDbWithOneUser();
    }

    @AfterAll
    public static void clearDatabase() {
        TestUtil.clearDatabase();
    }

    @Test
    void doPost_shouldWriteToResponseTokenDtoWithTokenAsJson() throws IOException, ServletException {
        LoginDto loginDto = new LoginDto(EXISTING_USER_NAME, EXISTING_USER_PASSWORD);
        String json = mapper.writeValueAsString(loginDto);
        OutputStream out = new ByteArrayOutputStream();

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        when(response.getWriter()).thenReturn(new PrintWriter(out));

        loginServlet.doPost(request, response);

        TokenDto tokenDto = assertDoesNotThrow(() -> mapper.readValue(out.toString(), TokenDto.class));
        assertNotNull(tokenDto);
        assertNotNull(tokenDto.getToken());
    }

    @Test
    void doPost_shouldSetResponseStatusCodeToForbidden_whenPasswordIsWrong() throws IOException, ServletException {
        LoginDto loginDto = new LoginDto(EXISTING_USER_NAME, WRONG_PASSWORD);
        String json = mapper.writeValueAsString(loginDto);
        OutputStream out = new ByteArrayOutputStream();

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        loginServlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN));
    }

    @Test
    void doPost_shouldSetResponseStatusCodeToBadRequest_whenLoginIsBlank() throws IOException, ServletException {
        LoginDto loginDto = new LoginDto(" ", WRONG_PASSWORD);
        String json = mapper.writeValueAsString(loginDto);
        OutputStream out = new ByteArrayOutputStream();

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        loginServlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST));
    }

    @Test
    void doPost_shouldSetResponseStatusCodeToForbidden_whenBadFormatJson() throws IOException, ServletException {
        String json = "wrong format";
        OutputStream out = new ByteArrayOutputStream();

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        loginServlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST));
    }
}