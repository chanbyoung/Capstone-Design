package durikkiri.project.entity.post;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.BaseEntity;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.post.FieldUpdateDto;
import durikkiri.project.entity.dto.post.PostUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Field> fieldList;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Apply> appliesList;

    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status; //모집현황
    @Enumerated(EnumType.STRING)
    private List<TechnologyStack> technologyStackList;
    private Long viewCount;
    private Long likeCount;

    public void updatePost(PostUpdateDto postUpdateDto) {
        this.title = postUpdateDto.getTitle();
        this.content = postUpdateDto.getContent();
        this.technologyStackList =postUpdateDto.getTechnologyStackList();
        if (!postUpdateDto.getCategory().equals(Category.GENERAL)) { //일반글일 경우 필드 수정 로직 실행 안함
            fieldUpdate(postUpdateDto);
        }
    }

    private void fieldUpdate(PostUpdateDto postUpdateDto) {
        Set<FieldCategory> processedFieldCategories = new HashSet<>();
        for (FieldUpdateDto fieldUpdateDto : postUpdateDto.getFieldList()) {
            Optional<Field> matchingField = fieldList.stream()
                    .filter(field -> field.getFieldCategory().equals(fieldUpdateDto.getFieldCategory()))
                    .findFirst();
            if (matchingField.isPresent()) {
                matchingField.get().updateField(fieldUpdateDto);
            }
            else {
                Field newField = fieldUpdateDto.toEntity(this);
                this.fieldList.add(newField);
            }
            processedFieldCategories.add(fieldUpdateDto.getFieldCategory());
        }
        this.fieldList.removeIf(field -> !processedFieldCategories.contains(field.getFieldCategory()));
    }

    public void updateViewCount() {
        this.viewCount ++;
    }

    public void updateComment(Comment comment) {
        this.commentList.add(comment);
    }

    public void updateStatus() {
        boolean allBalanced = this.fieldList.stream()
                .allMatch(field ->
                        field.getCurrentRecruitment() == field.getTotalRecruitment());
        if (allBalanced) {
            // 모든 field의 currentRecruitment와 totalRecruitment가 같은 경우
            // Post 엔티티 업데이트 로직 실행
            this.status = RecruitmentStatus.N;
        } else {
            this.status = RecruitmentStatus.Y;
        }
    }
}
