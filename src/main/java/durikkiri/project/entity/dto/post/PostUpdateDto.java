package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.TechnologyStack;
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

    private List<TechnologyStack> technologyStackList = new ArrayList<>();
    private List<FieldUpdateDto> fieldList = new ArrayList<>();

}
