package durikkiri.project.entity.dto;

import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HomeGetDto {
    private Long id;
    private String title;
    private Category category;
    private String createdBy;

    public static HomeGetDto toDto(Post post) {
        return HomeGetDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .createdBy(post.getCreatedBy())
                .build();
    }
}
