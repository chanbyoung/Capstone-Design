package durikkiri.project.entity.post;

import durikkiri.project.entity.dto.post.FieldDto;
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

    public void updateField(FieldDto fieldDto) {
        if (fieldDto.getTotalRecruitment() < currentRecruitment) {
            throw new RecruitmentException("Total recruitment cannot be less than current recruitment.");
        }
        if (totalRecruitment == 0) {
            throw new RecruitmentException("Total recruitment cannot be zero");
        }
        this.totalRecruitment = fieldDto.getTotalRecruitment();
    }

    public void updateCurrentRecruitment(Boolean flag) {
        if(flag) {
            if (currentRecruitment + 1 <= totalRecruitment) {
                currentRecruitment++;
            } else {
                throw new RecruitmentException("Current recruitment cannot exceed total recruitment.");
            }
        }
        else {
            currentRecruitment --;
        }
    }
}
