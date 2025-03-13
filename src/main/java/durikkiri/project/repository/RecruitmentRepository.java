package durikkiri.project.repository;

import durikkiri.project.entity.post.RecruitmentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentRepository extends JpaRepository<RecruitmentInfo, Long> {

}
