package durikkiri.project.dto.post;

import durikkiri.project.entity.post.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostUpdateDto {
    @NotBlank
    private String title;
    private Category category;
    @NotBlank
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<String> technologyStackList = new ArrayList<>();
    private List<FieldDto> fieldList = new ArrayList<>();

}
