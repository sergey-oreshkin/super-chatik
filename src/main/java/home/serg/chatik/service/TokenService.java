package home.serg.chatik.service;

import home.serg.chatik.dao.user.User;
import home.serg.chatik.dto.TokenDto;

public interface TokenService {
    TokenDto registerToken(User user);

    String getUsername(String token);
}
