package home.serg.chatik.context;

import home.serg.chatik.service.MessageService;
import home.serg.chatik.service.impl.MessageServiceImpl;

public enum MessageContext {
    MESSAGE_SERVICE(new MessageServiceImpl());

    private final MessageService messageService;

    MessageContext(MessageService messageService) {
        this.messageService = messageService;
    }

    public MessageService getInstance() {
        return messageService;
    }
}
