package durikkiri.project.dto.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCreateDto {
    private Long postId;
    private String content;
    @NotBlank
    private Long receiverId;
}
