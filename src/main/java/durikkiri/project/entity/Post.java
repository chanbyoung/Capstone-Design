package durikkiri.project.entity;

import durikkiri.project.entity.dto.post.FieldUpdateDto;
import durikkiri.project.entity.dto.post.PostUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

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

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Field> fieldList;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> commentList;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status; //모집현황
    private Long viewCount;
    private Long likeCount;

    public void updatePost(PostUpdateDto postUpdateDto) {
        this.title = postUpdateDto.getTitle();
        this.content = postUpdateDto.getContent();
        for (FieldUpdateDto fieldUpdateDto : postUpdateDto.getFieldList()) {
            Optional<Field> matchingField = fieldList.stream()
                    .filter(field -> field.getFieldCategory().equals(fieldUpdateDto.getFieldCategory()))
                    .findFirst();
            if (matchingField.isPresent()) {
                matchingField.get().updateField(fieldUpdateDto);
            }
            else {
                Field newField = fieldUpdateDto.toValue();
                this.fieldList.add(newField);
            }
        }

    }

    public void updateViewCount() {
        this.viewCount ++;
    }

    public void updateComment(Comment comment) {
        this.commentList.add(comment);
    }
}
