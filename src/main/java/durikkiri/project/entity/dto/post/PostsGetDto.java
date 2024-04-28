package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.TechnologyStack;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostsGetDto {
    private Long id;
    private String title;
    private List<TechnologyStack> technologyStackList;
    private Long viewCount;
    private Long likeCount;
    static public PostsGetDto toDto(Post post) {
        return PostsGetDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .technologyStackList(post.getTechnologyStackList())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount()).build();
    }

}
