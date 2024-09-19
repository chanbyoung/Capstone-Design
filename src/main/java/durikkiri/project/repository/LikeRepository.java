package durikkiri.project.repository;

import durikkiri.project.entity.post.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("select l from Like l join fetch l.post p join fetch l.member m where p.id = :postId and m.loginId = :loginId")
    Optional<Like> findByPostIdAndMemberId(@Param("postId") Long postId,@Param("loginId") String memberLoginId);
}
