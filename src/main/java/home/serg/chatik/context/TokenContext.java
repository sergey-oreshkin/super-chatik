package home.serg.chatik.context;

import home.serg.chatik.service.TokenService;
import home.serg.chatik.service.impl.TokenServiceImpl;

public enum TokenContext {
    TOKEN_SERVICE(new TokenServiceImpl());

    private TokenService tokenService;

    TokenContext(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public TokenService getInstance() {
        return tokenService;
    }
}
