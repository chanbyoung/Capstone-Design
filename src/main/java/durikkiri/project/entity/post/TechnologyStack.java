package durikkiri.project.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechnologyStack {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "technology_stack_id")
    private Long id;

    private String name;

    @Builder
    public TechnologyStack(String name) {
        this.name = name;
    }
}
