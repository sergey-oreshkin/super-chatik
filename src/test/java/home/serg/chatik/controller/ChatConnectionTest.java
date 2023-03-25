package home.serg.chatik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.serg.chatik.TestUtil;
import home.serg.chatik.dto.MessageDto;
import home.serg.chatik.util.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.security.Principal;
import java.sql.*;

import static home.serg.chatik.TestUtil.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatConnectionTest {
    @Mock
    private Session session;
    @Mock
    private RemoteEndpoint.Basic basicRemote;

    private final Principal principal = () -> EXISTING_USER_NAME;
    private final ObjectMapper mapper = new ObjectMapper();

    private final ChatConnection connection = new ChatConnection();

    @BeforeAll
    public static void initDatabase() {
        initDbWithOneUser();
        addBlockedUser();
    }

    @AfterAll
    public static void clearDatabase() {
        TestUtil.clearDatabase();
    }

    private static void addBlockedUser() {
        String sql = "INSERT INTO users (username, password, role_id, blocked) VALUES (?,?,(SELECT id FROM role WHERE name=?),?)";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, BLOCKED_USER.getUsername());
            preparedStatement.setString(2, BLOCKED_USER.getPassword());
            preparedStatement.setString(3, BLOCKED_USER.getRole().name());
            preparedStatement.setBoolean(4, BLOCKED_USER.getBlocked());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) throw new SQLException("Affected rows not equals 1");
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (!generatedKeys.next()) throw new SQLException("Getting generated key is failed");
            BLOCKED_USER.setId(generatedKeys.getLong(1));
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Test
    void start_shouldInvokeSendTextWithEmptyListJson() throws IOException {
        when(session.getUserPrincipal()).thenReturn(principal);
        when(session.getBasicRemote()).thenReturn(basicRemote);

        connection.start(session);

        verify(basicRemote).sendText(eq("[]"));
    }

    @Test
    void incoming_shouldInvokeSendTextWithMessageDtoJson() throws IOException {
        when(session.getUserPrincipal()).thenReturn(principal);
        when(session.getBasicRemote()).thenReturn(basicRemote);

        connection.start(session);
        connection.incoming(DEFAULT_MESSAGE);

        String json = mapper.writeValueAsString(new MessageDto(EXISTING_USER_NAME, DEFAULT_MESSAGE));

        verify(basicRemote).sendText(eq(json));
    }

    @Test
    void incoming_shouldInvokeSendTextWithForbiddenMessage() throws IOException {
        when(session.getUserPrincipal()).thenReturn(() -> BLOCKED_USER_NAME);
        when(session.getBasicRemote()).thenReturn(basicRemote);

        connection.start(session);
        connection.incoming(DEFAULT_MESSAGE);

        String json = mapper.writeValueAsString(new MessageDto(ChatConnection.SYSTEM_USERNAME, ChatConnection.FORBIDDEN_MESSAGE));

        verify(basicRemote).sendText(eq(json));
    }
}