package home.serg.chatik.service;

import home.serg.chatik.dto.LoginDto;
import home.serg.chatik.dto.TokenDto;

public interface LoginService {
    TokenDto login(LoginDto loginDto);
}
