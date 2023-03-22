package home.serg.chatik.context;

import home.serg.chatik.service.LoginService;
import home.serg.chatik.service.impl.LoginServiceImpl;

public enum LoginContext {
    LOGIN_SERVICE(new LoginServiceImpl());
    private LoginService loginService;

    LoginContext(LoginService loginService) {
        this.loginService = loginService;
    }

    public LoginService getInstance() {
        return loginService;
    }
}
