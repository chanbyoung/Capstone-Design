package durikkiri.project.repository;

import durikkiri.project.entity.post.TechnologyStack;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TechnologyStackRepository extends JpaRepository<TechnologyStack, Long> {

    List<TechnologyStack> findByNameIn(List<String> technologyStackList);
}
