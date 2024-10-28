package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.TechnologyStack;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostSearchContent {
    private Category category;
    private String title;
    private String createdBy;
    private List<TechnologyStack> technologyStackList;
    private Long cursorId;
    private LocalDateTime cursorCreatedAt;

    public PostSearchContent(Category category, String title, String createdBy, List<TechnologyStack> technologyStackList, Long cursorId, LocalDateTime cursorCreatedAt) {
        this.category = category;
        this.title = title;
        this.createdBy = createdBy;
        this.technologyStackList = technologyStackList;
        this.cursorId = cursorId;
        this.cursorCreatedAt = cursorCreatedAt;
    }
}
