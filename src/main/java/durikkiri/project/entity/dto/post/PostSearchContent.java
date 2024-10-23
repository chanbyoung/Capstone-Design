package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.post.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostSearchContent {
    private Category category;
    private String title;
    private Long cursorId;
    private LocalDateTime cursorCreatedAt;

    public PostSearchContent(Category category, String title,Long cursorId, LocalDateTime cursorCreatedAt) {
        this.category = category;
        this.title = title;
        this.cursorId = cursorId;
        this.cursorCreatedAt = cursorCreatedAt;
    }
}
