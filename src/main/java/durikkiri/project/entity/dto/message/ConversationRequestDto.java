package durikkiri.project.entity.dto.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationRequestDto {
    private Long postId;
    private Long receiverId;
}
