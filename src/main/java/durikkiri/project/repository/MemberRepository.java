package durikkiri.project.repository;

import durikkiri.project.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m join fetch m.roles where m.loginId = :loginId")
    Optional<Member> findByLoginId(@Param("loginId") String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
    boolean existsByEmailAndUsername(String email, String username);
    boolean existsByEmailAndUsernameAndLoginId(String email, String username,String loginId);

    Optional<Member> findMemberByEmail(String email);
}
