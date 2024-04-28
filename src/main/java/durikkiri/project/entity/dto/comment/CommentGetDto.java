package durikkiri.project.entity.dto.comment;

import durikkiri.project.entity.post.Comment;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class CommentGetDto {
    private String memberName;
    private String content;

    static public CommentGetDto toDto(Comment comment) {
        return CommentGetDto.builder()
                .memberName(comment.getCreatedBy())
                .content(comment.getContent())
                .build();
    }
}
