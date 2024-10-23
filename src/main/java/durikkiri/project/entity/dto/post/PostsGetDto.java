package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.TechnologyStack;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostsGetDto {
    private Long id;
    private String title;
    private Category category;
    private List<TechnologyStack> technologyStackList;
    private String createdBy;
    private LocalDateTime createdAt;
    private Long viewCount;
    private Long likeCount;
    static public PostsGetDto toDto(Post post) {
        return PostsGetDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .createdBy(post.getCreatedBy())
                .createdAt(post.getCreatedAt())
                .technologyStackList(post.getTechnologyStackList())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount()).build();
    }

}
