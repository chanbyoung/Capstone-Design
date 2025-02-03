package durikkiri.project.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class RecruitmentTechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_tech_stack_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recruitment_info_id")
    private RecruitmentInfo recruitmentInfo;

    @ManyToOne
    @JoinColumn(name = "technology_stack_id")
    private TechnologyStack technologyStack;
}
