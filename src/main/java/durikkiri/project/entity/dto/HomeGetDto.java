package durikkiri.project.entity.dto;

import durikkiri.project.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HomeGetDto {
    private Long id;
    private String title;

    public static HomeGetDto toDto(Post post) {
        return HomeGetDto.builder()
                .id(post.getId())
                .title(post.getTitle()).build();
    }
}
