package durikkiri.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @Enumerated(EnumType.STRING)
    private FieldCategory fieldCategory;
    private int currentRecruitment;
    private int totalRecruitment;


    public void updatePost(Post post) {
        this.post = post;
    }
}
