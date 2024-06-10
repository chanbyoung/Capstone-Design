package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Field;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.TechnologyStack;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static durikkiri.project.entity.post.RecruitmentStatus.*;

@Getter
@Setter
public class PostAddDto {

    @NotBlank
    private String title;

    @NotNull
    private Category category;

    @NotBlank
    private String content;
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private List<TechnologyStack> technologyStackList = new ArrayList<>();

    private List<FieldAddDto> fieldList= new ArrayList<>();


    public Post toEntity(Member member) {
        Post post = Post.builder()
                .title(title)
                .category(category)
                .member(member)
                .commentList(new ArrayList<>())
                .technologyStackList(technologyStackList)
                .fieldList(new ArrayList<>()) // 빈 리스트로 초기화
                .content(content)
                .status(Y)
                .likeCount(0L)
                .viewCount(0L)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // fieldList의 각 Field에 현재 Post를 설정
        for (FieldAddDto fieldAddDto : fieldList) {
            Field field = fieldAddDto.toEntity(post);
            post.getFieldList().add(field);
        }

        return post;
    }

}
