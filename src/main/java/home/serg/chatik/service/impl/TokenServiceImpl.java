package home.serg.chatik.service.impl;

import home.serg.chatik.context.DaoContext;
import home.serg.chatik.dao.token.Token;
import home.serg.chatik.dao.token.TokenRepository;
import home.serg.chatik.dao.user.User;
import home.serg.chatik.dto.TokenDto;
import home.serg.chatik.exception.TokenNotFoundException;
import home.serg.chatik.mapper.TokenMapper;
import home.serg.chatik.service.TokenService;

import java.util.Optional;
import java.util.UUID;

public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository = (TokenRepository) DaoContext.TOKEN_REPOSITORY.getInstance();
    private final TokenMapper tokenMapper = new TokenMapper();

    @Override
    public TokenDto registerToken(User user) {
        Optional<Token> result = tokenRepository.getByUserId(user.getId());
        if (result.isEmpty()){
            return tokenMapper.toDto(tokenRepository.save(new Token(getNewToken(), user)));
        }
        Token token = result.get();
        token.setToken(getNewToken());
        return tokenMapper.toDto(tokenRepository.update(token));
    }

    @Override
    public String getUsername(String token) {
        return tokenRepository.getByToken(token)
                .orElseThrow(()-> new TokenNotFoundException("Token not found"))
                .getUser().getUsername();
    }

    private String getNewToken(){
        return UUID.randomUUID().toString();
    }
}
