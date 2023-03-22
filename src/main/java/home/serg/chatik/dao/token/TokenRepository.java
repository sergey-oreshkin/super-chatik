package home.serg.chatik.dao.token;

import home.serg.chatik.dao.DAO;

import java.util.Optional;

public class TokenRepository implements DAO<Token, Long> {
    @Override
    public Token findById(Long id) {
        return null;
    }

    @Override
    public Token save(Token entity) {
        return null;
    }

    @Override
    public Token update(Token entity) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    public Optional<Token> getByToken(String token){
        return null;
    }

    public Optional<Token> getByUserId(Long userId){
        return Optional.empty();
    }
}
