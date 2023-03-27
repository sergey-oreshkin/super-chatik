package home.serg.chatik.dao.token;

import home.serg.chatik.dao.user.User;
import home.serg.chatik.util.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static home.serg.chatik.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class TokenRepositoryTest {

    static User user = new User();

    static {
        user.setId(1L);
    }

    public static final Token EXICTING_TOKEN = new Token("test token", user);
    private final TokenRepository tokenRepository = new TokenRepository();

    @BeforeAll
    public static void initDb() {
        initDbWithOneUser();
        addTokenToDb();
    }

    @AfterAll
    public static void clearDb() {
        clearDatabase();
    }

    @Test
    void findById_shouldReturnExistingToken() {
        Optional<Token> optionalToken = tokenRepository.findById(1L);

        assertTrue(optionalToken.isPresent());

        Token token = optionalToken.get();

        assertAll(
                () -> assertEquals(DEFAULT_USER_ID, token.getId()),
                () -> assertEquals(EXICTING_TOKEN.getToken(), token.getToken()),
                () -> assertEquals(EXICTING_TOKEN.getUser().getId(), token.getUser().getId())
        );
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenTokenNotFound() {
        Optional<Token> optionalToken = tokenRepository.findById(WRONG_ID);

        assertTrue(optionalToken.isEmpty());
    }

    @Test
    void save_shouldSaveTokenToDbAndReturnTokenWithId() {
        Token tokenForSave = new Token("save token", user);

        Token token = tokenRepository.save(tokenForSave);

        assertNotNull(token);
        assertNotNull(token.getId());
    }

    @Test
    void update_shouldUpdateTokenInDbAndReturnToken() {
        String oldToken = "old token";
        String newToken = "new token";
        Token token = new Token(oldToken, user);
        Token savedToken = tokenRepository.save(token);
        savedToken.setToken(newToken);
        tokenRepository.update(savedToken);
        Optional<Token> updatedToken = tokenRepository.findById(savedToken.getId());

        assertTrue(updatedToken.isPresent());

        assertEquals(newToken, updatedToken.get().getToken());
    }

    @Test
    void deleteById_shouldDeleteTokenFromDb() {
        Token tokenForDelete = new Token("", user);
        Token token = tokenRepository.save(tokenForDelete);

        assertTrue(tokenRepository.deleteById(token.getId()));

        Token deletedToken = tokenRepository.findById(token.getId()).orElse(null);

        assertNull(deletedToken);
    }

    @Test
    void getByToken_shouldReturnExistingToken() {
        Optional<Token> optionalToken = tokenRepository.getByToken(EXICTING_TOKEN.getToken());

        assertTrue(optionalToken.isPresent());

        Token token = optionalToken.get();

        assertAll(
                () -> assertEquals(DEFAULT_USER_ID, token.getId()),
                () -> assertEquals(EXICTING_TOKEN.getToken(), token.getToken()),
                () -> assertEquals(EXICTING_TOKEN.getUser().getId(), token.getUser().getId())
        );
    }

    @Test
    void getByToken_shouldReturnEmptyOptional_whenTokenNotFound() {
        Optional<Token> optionalToken = tokenRepository.getByToken("WRONG TOKEN");

        assertTrue(optionalToken.isEmpty());
    }

    @Test
    void getByUserId_shouldReturnExistingToken() {
        Optional<Token> optionalToken = tokenRepository.getByUserId(EXICTING_TOKEN.getUser().getId());

        assertTrue(optionalToken.isPresent());

        Token token = optionalToken.get();

        assertAll(
                () -> assertEquals(DEFAULT_USER_ID, token.getId()),
                () -> assertEquals(EXICTING_TOKEN.getToken(), token.getToken()),
                () -> assertEquals(EXICTING_TOKEN.getUser().getId(), token.getUser().getId())
        );
    }

    @Test
    void getByUserId_shouldReturnEmptyOptional_whenUserNotFound() {
        Optional<Token> optionalToken = tokenRepository.getByUserId(WRONG_ID);

        assertTrue(optionalToken.isEmpty());
    }

    private static void addTokenToDb() {
        String sql = "INSERT INTO token (token, user_id) VALUES (?,?)";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, EXICTING_TOKEN.getToken());
            preparedStatement.setLong(2, EXICTING_TOKEN.getUser().getId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) throw new SQLException("Affected rows not equals 1");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}