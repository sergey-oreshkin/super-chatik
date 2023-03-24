package home.serg.chatik.service.impl;

import home.serg.chatik.context.DaoContext;
import home.serg.chatik.dao.message.Message;
import home.serg.chatik.dao.message.MessageRepository;
import home.serg.chatik.dao.user.User;
import home.serg.chatik.dao.user.UserRepository;
import home.serg.chatik.dto.MessageDto;
import home.serg.chatik.exception.MessageException;
import home.serg.chatik.mapper.MessageMapper;
import home.serg.chatik.service.MessageService;
import home.serg.chatik.util.HTMLFilter;

import java.util.List;

public class MessageServiceImpl implements MessageService {
    private static final int LAST_MESSAGES_COUNT = 20;

    private final MessageRepository messageRepository = (MessageRepository) DaoContext.MESSAGE_REPOSITORY.getInstance();
    private final UserRepository userRepository = (UserRepository) DaoContext.USER_REPOSITORY.getInstance();
    private final MessageMapper messageMapper = new MessageMapper();

    @Override
    public MessageDto validateAndSave(String username, String message) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getBlocked()) throw new MessageException("User is blocked");
        String text = HTMLFilter.filter(message);
        Message messageEntity = new Message(text, user);
        return messageMapper.toDto(messageRepository.save(messageEntity));
    }

    @Override
    public List<MessageDto> getLastMessages() {
        return messageMapper.toDto(messageRepository.getLastMessages(LAST_MESSAGES_COUNT));
    }
}
