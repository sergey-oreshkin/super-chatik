package home.serg.chatik.filter;

import home.serg.chatik.context.TokenContext;
import home.serg.chatik.exception.TokenNotFoundException;
import home.serg.chatik.service.TokenService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@WebFilter("/chat")
public class WebsocketTokenFilter implements Filter {
    public static final String TOKEN_PARAMETER_NAME = "token";

    private final TokenService tokenService = TokenContext.TOKEN_SERVICE.getInstance();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = request.getParameter(TOKEN_PARAMETER_NAME);
        if (token == null || token.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        try {
            String username = tokenService.getUsername(token);
            filterChain.doFilter(new AuthenticatedRequest(request, username), servletResponse);
        } catch (TokenNotFoundException ex) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private static class AuthenticatedRequest extends HttpServletRequestWrapper {
        private String username;

        public AuthenticatedRequest(HttpServletRequest request, String username) {
            super(request);
            this.username = username;
        }

        @Override
        public Principal getUserPrincipal() {
            return () -> username;
        }
    }
}
