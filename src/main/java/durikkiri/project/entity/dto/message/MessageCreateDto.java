package durikkiri.project.entity.dto.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCreateDto {
    private String content;
    private Long receiverId;
}
