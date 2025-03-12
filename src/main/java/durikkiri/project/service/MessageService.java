package durikkiri.project.service;

import durikkiri.project.dto.message.ConversationGetDto;
import durikkiri.project.dto.message.ConversationRequestDto;
import durikkiri.project.dto.message.ConversationsGetDto;
import durikkiri.project.dto.message.MessageCreateDto;
import durikkiri.project.dto.message.MessageDto;
import durikkiri.project.dto.message.MessageUpdateDto;

import java.util.List;

public interface MessageService {

    void sendMessage(MessageCreateDto messageCreateDto, Long memberId);

    MessageDto getMessage(Long messageId);

    void updateMessage(Long messageId, MessageUpdateDto messageUpdateDto, Long memberId);

    void deleteMessage(Long messageId, Long memberId);


    List<ConversationsGetDto> getConversationFromMember(Long memberId);

    ConversationGetDto getConversation(Long conversationId, Long memberId);

    ConversationGetDto createOrRetrieveConversation(ConversationRequestDto conversationRequestDto, Long memberId);
}
