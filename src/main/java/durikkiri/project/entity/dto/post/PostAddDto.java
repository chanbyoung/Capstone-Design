package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Category;
import durikkiri.project.entity.Field;
import durikkiri.project.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostAddDto {

    @NotBlank
    private String title;

    @NotNull
    private Category category;

    @NotBlank
    private String content;

    private List<FieldAddDto> fieldList= new ArrayList<>();


    public Post toEntity() {
        Post post = Post.builder()
                .title(title)
                .category(category)
                .fieldList(new ArrayList<>()) // 빈 리스트로 초기화
                .content(content)
                .likeCount(0L)
                .viewCount(0L)
                .build();

        // fieldList의 각 Field에 현재 Post를 설정
        for (FieldAddDto fieldAddDto : fieldList) {
            Field field = fieldAddDto.toEntity(post);
            post.getFieldList().add(field);
        }

        return post;
    }

}
