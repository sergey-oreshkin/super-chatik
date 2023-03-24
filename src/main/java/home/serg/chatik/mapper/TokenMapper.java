package home.serg.chatik.mapper;

import home.serg.chatik.dao.token.Token;
import home.serg.chatik.dto.TokenDto;

public class TokenMapper {
    public TokenDto toDto(Token token) {
        return new TokenDto(token.getToken());
    }
}
