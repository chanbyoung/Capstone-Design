package durikkiri.project.entity.post;

import durikkiri.project.entity.Apply;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecuruitmentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recuruitment_info_id")
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "recruitmentInfo", cascade = CascadeType.PERSIST)
    private List<TechnologyStack> technologySkillList;

    @OneToMany(mappedBy = "recruitmentInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Field> fieldList;

    @OneToMany(mappedBy = "recruitmentInfo", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Apply> appliesList;


    public void updateStatus() {
        boolean recruitmentDeadline = this.fieldList.stream()
                .allMatch(field ->
                        field.getCurrentRecruitment() == field.getTotalRecruitment());
        boolean dateDeadline= endDate.isBefore(LocalDate.now());
        if (recruitmentDeadline || dateDeadline) {
            // 모든 field의 currentRecruitment와 totalRecruitment가 같은 경우
            // Post 엔티티 업데이트 로직 실행
            this.status = "closed";
        } else {
            this.status = "open";
        }
    }




}
