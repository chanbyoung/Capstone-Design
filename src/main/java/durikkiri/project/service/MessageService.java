package durikkiri.project.service;

import durikkiri.project.entity.dto.message.*;

import java.util.List;

public interface MessageService {

    void sendMessage(MessageCreateDto messageCreateDto);

    MessageDto getMessage(Long messageId);

    void updateMessage(Long messageId, MessageUpdateDto messageUpdateDto);

    void deleteMessage(Long messageId);


    List<ConversationGetsDto> getConversationFromMember();

    ConversationGetDto getConversation(Long conversationId);
}
