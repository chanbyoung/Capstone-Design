package durikkiri.project.entity.dto.comment;


import durikkiri.project.entity.post.Comment;
import durikkiri.project.entity.post.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    @NotBlank
    private String content;

    public Comment toEntity(Post post) {
        return Comment.builder()
                .post(post)
                .content(content)
                .build();
    }
}
