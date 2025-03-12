package durikkiri.project.dto.post;

import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GeneralPostGetDto {
    private Long postId;
    private String title;
    private Category category;
    private String content;
    private String memberNickName;
    private Long viewCount;
    private Long likeCount;
    private Boolean isLiked;
    private Boolean isOwner;

    public static GeneralPostGetDto toDto(Post post, PostUserStatusDto postUserStatusDto) {
        return GeneralPostGetDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .content(post.getContent())
                .memberNickName(post.getMember().getNickname())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .isLiked(postUserStatusDto.getLiked())
                .isLiked(postUserStatusDto.getOwner())
                .build();

    }

}
