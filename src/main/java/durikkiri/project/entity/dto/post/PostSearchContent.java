package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchContent {
    private Category category;
    private String title;

    public PostSearchContent(Category category, String title) {
        this.category = category;
        this.title = title;
    }
}
