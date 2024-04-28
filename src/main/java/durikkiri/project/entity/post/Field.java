package durikkiri.project.entity.post;

import durikkiri.project.entity.dto.post.FieldUpdateDto;
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

    public void updateField(FieldUpdateDto fieldUpdateDto) {
        this.totalRecruitment = fieldUpdateDto.getTotalRecruitment();
    }

    public void updateCurrentRecruitment() {
        if (currentRecruitment + 1 != totalRecruitment) {
            currentRecruitment++;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
