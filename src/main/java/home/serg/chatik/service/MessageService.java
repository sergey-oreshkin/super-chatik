package home.serg.chatik.service;

import home.serg.chatik.dto.MessageDto;

import java.util.List;

public interface MessageService {
    MessageDto validateAndSave(String username, String message);

    List<MessageDto> getLastMessages();
}
