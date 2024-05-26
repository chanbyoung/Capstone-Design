package durikkiri.project.service;

import durikkiri.project.entity.Message;
import durikkiri.project.entity.dto.message.MessageCreateDto;
import durikkiri.project.entity.dto.message.MessageDto;
import durikkiri.project.entity.dto.message.MessageUpdateDto;

import java.util.List;

public interface MessageService {
    List<MessageDto> getMessagesForMember();

    void sendMessage(MessageCreateDto messageCreateDto);

    MessageDto getMessage(Long messageId);

    void updateMessage(Long messageId, MessageUpdateDto messageUpdateDto);

    void deleteMessage(Long messageId);
}
