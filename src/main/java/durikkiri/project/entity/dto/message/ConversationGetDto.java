package durikkiri.project.entity.dto.message;

import durikkiri.project.entity.Conversation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ConversationGetDto {
    private Long id;
    private LocalDateTime recentMessage;
    private Long postId;
    private Long opponentId;
    private List<MessageDto> messageList;

    static public ConversationGetDto toDto(Conversation conversation, Long opponentId) {
        List<MessageDto> messageList = conversation.getMessages().stream()
                .map(MessageDto::toDto)
                .collect(Collectors.toList());
        return ConversationGetDto.builder()
                .id(conversation.getId())
                .opponentId(opponentId)
                .postId(conversation.getPost().getId())
                .recentMessage(conversation.getModifiedAt())
                .messageList(messageList).build();

    }
}
