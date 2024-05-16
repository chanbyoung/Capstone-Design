package durikkiri.project.entity;

import durikkiri.project.entity.dto.apply.ApplyUpdateDto;
import durikkiri.project.entity.post.Field;
import durikkiri.project.entity.post.FieldCategory;
import durikkiri.project.entity.post.Post;
import jakarta.persistence.*;
import lombok.*;

import static durikkiri.project.entity.ApplyStatus.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Apply extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Enumerated(EnumType.STRING)
    private FieldCategory fieldCategory;
    @Enumerated(EnumType.STRING)
    private ApplyStatus applyStatus;

    public void updateStatus(ApplyStatus applyStatus) {
        this.applyStatus = applyStatus;
    }

    public void postFieldUpdate() {
        Field field = post.getFieldList().stream()
                .filter(f -> f.getFieldCategory().equals(fieldCategory))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Field not found"));
        try {
            field.updateCurrentRecruitment();
            this.applyStatus = ACCEPT;
        } catch (IllegalArgumentException e) {
            // 추가적인 예외 처리 (예: 로깅)를 여기에 넣을 수 있습니다.
            throw new IllegalArgumentException("apply deadline");
        }
    }

    @Override
    public String toString() {
        return "Apply{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", post=" + post +
                ", fieldCategory=" + fieldCategory +
                ", applyStatus=" + applyStatus +
                '}';
    }

    public void updateContent(ApplyUpdateDto applyUpdateDto) {
        this.content = applyUpdateDto.getContent();
    }
}
