package durikkiri.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import durikkiri.project.entity.dto.post.FieldUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    @Enumerated(EnumType.STRING)
    private FieldCategory fieldCategory;
    private int currentRecruitment;
    private int totalRecruitment;

    public void updateField(FieldUpdateDto fieldUpdateDto) {
        this.totalRecruitment = fieldUpdateDto.getTotalRecruitment();
    }
}
