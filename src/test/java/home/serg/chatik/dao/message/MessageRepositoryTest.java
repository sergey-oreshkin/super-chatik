package home.serg.chatik.dao.message;

import home.serg.chatik.dao.token.Token;
import home.serg.chatik.dao.user.User;
import home.serg.chatik.util.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static home.serg.chatik.TestUtil.*;
import static home.serg.chatik.TestUtil.WRONG_ID;
import static org.junit.jupiter.api.Assertions.*;

class MessageRepositoryTest {
    static User user = new User();

    static {
        user.setId(1L);
    }

    public static final Message EXISTING_MESSAGE = new Message("test message", user);
    private final MessageRepository messageRepository = new MessageRepository();

    @BeforeAll
    public static void initDb() {
        initDbWithOneUser();
        addMessageToDb();
    }

    @AfterAll
    public static void clearDb() {
        clearDatabase();
    }

    @Test
    void findById_shouldReturnExistingMessage() {
        Optional<Message> optionalMessage = messageRepository.findById(1L);

        assertTrue(optionalMessage.isPresent());

       Message message = optionalMessage.get();

        assertAll(
                () -> assertEquals(1L, message.getId()),
                () -> assertEquals(EXISTING_MESSAGE.getMessage(), message.getMessage()),
                () -> assertEquals(EXISTING_MESSAGE.getUser().getId(),message.getUser().getId())
        );
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenUserNotFound() {
        Optional<Message> optionalMessage = messageRepository.findById(WRONG_ID);

        assertTrue(optionalMessage.isEmpty());
    }

    @Test
    void save_shouldSaveMessageToDbAndReturnMessageWithId() {
        Message messageForSave = new Message("new message", user);
        Message message = messageRepository.save(messageForSave);

        assertNotNull(message);
        assertNotNull(message.getId());
    }

    @Test
    void update_shouldUpdateMessageInDbAndReturnMessage() {
        String oldMessage = "old message";
        String newMessage = "new message";
        Message message = new Message(oldMessage, user);
        Message savedMessage = messageRepository.save(message);
        savedMessage.setMessage(newMessage);
        messageRepository.update(message);
        Optional<Message> updatedMessage = messageRepository.findById(savedMessage.getId());

        assertTrue(updatedMessage.isPresent());

        assertEquals(newMessage, updatedMessage.get().getMessage());
    }

    @Test
    void deleteById_shouldDeleteMessageFromDb() {
        Message messageForDelete = new Message("to delete", user);
        Message message = messageRepository.save(messageForDelete);

        assertTrue(messageRepository.deleteById(message.getId()));

        Message deletedMessage = messageRepository.findById(message.getId()).orElse(null);

        assertNull(deletedMessage);
    }
    @Test
    void getLastMessages_shouldReturnGivenNumberOrLessMessages() {
        int count = 10;
        List<Message> lastMessages = messageRepository.getLastMessages(count);

        assertTrue(lastMessages.size() <= count);
    }

    private static void addMessageToDb(){
        String sql = "INSERT INTO message (message, user_id) VALUES (?,?)";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, EXISTING_MESSAGE.getMessage());
            preparedStatement.setLong(2, EXISTING_MESSAGE.getUser().getId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) throw new SQLException("Affected rows not equals 1");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}