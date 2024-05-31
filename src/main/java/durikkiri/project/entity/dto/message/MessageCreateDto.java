package durikkiri.project.entity.dto.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCreateDto {
    private String content;
    @NotBlank
    private Long receiverId;
}
