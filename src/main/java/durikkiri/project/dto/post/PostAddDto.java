package durikkiri.project.dto.post;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class PostAddDto {

    @NotBlank
    private String title;

    @NotNull
    private Category category;

    @NotBlank
    private String content;

    private RecruitmentAddDto recruitmentAddDto;

    public Post toEntity(Member member) {

        Post post = Post.builder()
                .title(title)
                .category(category)
                .member(member)
                .commentList(new ArrayList<>())
                .content(content)
                .likeCount(0L)
                .viewCount(0L)
                .build();
        return post;
    }

    public boolean isGeneralCategory() {
        return category == Category.GENERAL;
    }
}
