package durikkiri.project.entity.dto.message;

import durikkiri.project.entity.Conversation;
import durikkiri.project.entity.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ConversationGetDto {
    private Long id;
    private LocalDateTime recentMessage;
    private List<MessageDto> messageList;

    static public ConversationGetDto toDto(Conversation conversation) {
        List<MessageDto> messageList = conversation.getMessages().stream()
                .map(MessageDto::toDto)
                .collect(Collectors.toList());
        return ConversationGetDto.builder()
                .id(conversation.getId())
                .recentMessage(conversation.getModifiedAt())
                .messageList(messageList).build();

    }
}
