package durikkiri.project.entity;

import static durikkiri.project.entity.ApplyStatus.UNREAD;

import durikkiri.project.dto.apply.ApplyAddDto;
import durikkiri.project.dto.apply.ApplyUpdateDto;
import durikkiri.project.entity.post.Field;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.RecruitmentInfo;
import jakarta.persistence.*;
import lombok.*;


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
    @JoinColumn(name = "recuruitment_info_id")
    private RecruitmentInfo recruitmentInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String fieldCategory;

    @Enumerated(EnumType.STRING)
    private ApplyStatus applyStatus;

    public void updateStatus(ApplyStatus applyStatus) {
        this.applyStatus = applyStatus;
    }

    public void postFieldUpdate(Boolean flag) {
        Field field = recruitmentInfo.getFieldList().stream()
                .filter(f -> f.getFieldCategory().equals(fieldCategory))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Field not found"));
        try {
            field.updateCurrentRecruitment(flag);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("apply deadline");
        }
    }

    public void updateContent(ApplyUpdateDto applyUpdateDto) {
        this.content = applyUpdateDto.getContent();
    }

    public static Apply of(ApplyAddDto applyAddDto, Post post, Member member) {
        return Apply.builder()
                .fieldCategory(applyAddDto.getFieldCategory())
                .recruitmentInfo(post.getRecruitmentInfo())
                .member(member)
                .content(applyAddDto.getContent())
                .applyStatus(UNREAD)
                .build();
    }
}
