package home.serg.chatik.mapper;

import home.serg.chatik.dao.message.Message;
import home.serg.chatik.dto.MessageDto;

import java.util.List;
import java.util.stream.Collectors;

public class MessageMapper {
    public MessageDto toDto(Message message) {
        return new MessageDto(message.getUser().getUsername(), message.getMessage());
    }

    public List<MessageDto> toDto(List<Message> messages) {
        return messages.stream().map(this::toDto).collect(Collectors.toList());
    }
}
