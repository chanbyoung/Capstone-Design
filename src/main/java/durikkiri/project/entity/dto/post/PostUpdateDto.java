package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Category;
import durikkiri.project.entity.TechnologyStack;
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
    private Category category;
    @NotBlank
    private String content;
    private List<TechnologyStack> technologyStackList = new ArrayList<>();
    private List<FieldUpdateDto> fieldList = new ArrayList<>();

}
