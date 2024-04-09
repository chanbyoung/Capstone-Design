package durikkiri.project.entity.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostUpdateDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private List<FieldUpdateDto> fieldList = new ArrayList<>();

}
