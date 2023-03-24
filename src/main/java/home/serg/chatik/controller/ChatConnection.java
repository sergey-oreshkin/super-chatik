package home.serg.chatik.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.serg.chatik.context.MessageContext;
import home.serg.chatik.dto.MessageDto;
import home.serg.chatik.exception.MessageException;
import home.serg.chatik.service.MessageService;

import javax.servlet.http.HttpServlet;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat")
public class ChatConnection extends HttpServlet {

    public static final String SYSTEM_USERNAME = "System";
    public static final String FORBIDDEN_MESSAGE = "Вам запрещено отправлять сообщения!";

    private static final Set<ChatConnection> connections = new CopyOnWriteArraySet<>();
    public static final ObjectMapper mapper = new ObjectMapper();

    private MessageService messageService = MessageContext.MESSAGE_SERVICE.getInstance();
    private Session session;
    private String nickname;

    @OnOpen
    public void start(Session session) throws JsonProcessingException {
        this.session = session;
        this.nickname = session.getUserPrincipal().getName();
        connections.add(this);
        List<MessageDto> lastMessages = messageService.getLastMessages();
        String json = mapper.writeValueAsString(lastMessages);
        sendJson(this, json);
        String text = String.format("* %s %s", nickname, "has joined.");
        broadcast(new MessageDto(SYSTEM_USERNAME, text));
    }

    @OnClose
    public void end() {
        connections.remove(this);
        if (nickname != null) {
            String text = String.format("* %s %s", nickname, "has disconnected.");
            broadcast(new MessageDto(SYSTEM_USERNAME, text));
        }
    }

    @OnMessage
    public void incoming(String message) throws JsonProcessingException {
        try {
            MessageDto messageDto = messageService.validateAndSave(nickname, message);
            broadcast(messageDto);
        } catch (MessageException ex) {
            String forbiddenMessage = mapper.writeValueAsString(new MessageDto(SYSTEM_USERNAME, FORBIDDEN_MESSAGE));
            sendJson(this, forbiddenMessage);
        }
    }

    private static void sendJson(ChatConnection connection, String json) {
        try {
            synchronized (connection) {
                connection.session.getBasicRemote().sendText(json);
            }
        } catch (IOException ex) {
            disconnect(connection);
        }
    }

    private static void broadcast(MessageDto msg) {
        String json;
        try {
            json = mapper.writeValueAsString(msg);
        } catch (JsonProcessingException ex) {
            //Ignore
            return;
        }
        for (ChatConnection connection : connections) {
            try {
                synchronized (connection) {
                    connection.session.getBasicRemote().sendText(json);
                }
            } catch (IOException ex) {
                disconnect(connection);
            }
        }
    }

    private static void disconnect(ChatConnection connection) {
        connections.remove(connection);
        try {
            connection.session.close();
        } catch (IOException ex1) {
            // Ignore
        }
        String text = String.format("* %s %s", connection.nickname, "has been disconnected.");
        MessageDto disconnectedMessage = new MessageDto(SYSTEM_USERNAME, text);
        broadcast(disconnectedMessage);
    }
}
