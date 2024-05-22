package durikkiri.project.entity.post;

import durikkiri.project.entity.dto.post.FieldUpdateDto;
import durikkiri.project.exception.RecruitmentException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @Enumerated(EnumType.STRING)
    private FieldCategory fieldCategory;
    private int currentRecruitment;
    private int totalRecruitment;

    public void updateField(FieldUpdateDto fieldUpdateDto ) {
        if (fieldUpdateDto.getTotalRecruitment() < currentRecruitment) {
            throw new RecruitmentException("Total recruitment cannot be less than current recruitment.");
        }
        if (totalRecruitment == 0) {
            throw new RecruitmentException("Total recruitment cannot be zero");
        }
        this.totalRecruitment = fieldUpdateDto.getTotalRecruitment();
    }

    public void updateCurrentRecruitment() {
        if (currentRecruitment + 1 <= totalRecruitment) {
            currentRecruitment++;
        } else {
            throw new RecruitmentException("Current recruitment cannot exceed total recruitment.");
        }
    }
}
