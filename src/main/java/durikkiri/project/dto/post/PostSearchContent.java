package durikkiri.project.dto.post;

import durikkiri.project.entity.post.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostSearchContent {
    private Category category;
    private String title;
    private Boolean withClosed;
    private Boolean createdByAsc;
    private List<String> technologyStackList;

}
