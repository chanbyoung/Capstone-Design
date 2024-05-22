package durikkiri.project.repository;

import durikkiri.project.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    @Query("select m from Member m join fetch m.postList where m.id = :id")
    Optional<Member> findMemberWithPost(@Param("id") Long id);

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);
}
