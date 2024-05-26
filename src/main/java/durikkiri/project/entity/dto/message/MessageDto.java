package durikkiri.project.entity.dto.message;

import durikkiri.project.entity.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MessageDto {
    private Long id;
    private String content;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime sentAt;

    public static MessageDto toDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .sentAt(message.getCreatedAt())
                .build();
    }
}
