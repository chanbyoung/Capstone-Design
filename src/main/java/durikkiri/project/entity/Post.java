package durikkiri.project.entity;

import durikkiri.project.entity.dto.post.PostUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private Category category;
    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Field> fieldList;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status; //모집현황
    private Long viewCount;
    private Long likeCount;


    public void updatePost(PostUpdateDto postUpdateDto) {
        this.title = postUpdateDto.getTitle();
        this.content = postUpdateDto.getContent();
    }

    public void updateViewCount() {
        this.viewCount ++;
    }
}
