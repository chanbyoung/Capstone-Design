package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Category;
import durikkiri.project.entity.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAddDto {

    @NotBlank
    private String title;

    private Category category;

    @NotBlank
    private String content;


    public Post toEntity() {
        return Post.builder()
                .title(title)
                .category(category)
                .content(content)
                .likeCount(0L)
                .viewCount(0L)
                .build();
    }

}
