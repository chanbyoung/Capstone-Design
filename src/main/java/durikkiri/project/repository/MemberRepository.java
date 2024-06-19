package durikkiri.project.repository;

import durikkiri.project.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m join fetch m.roles where m.loginId = :loginId")
    Optional<Member> findByLoginId(@Param("loginId") String loginId);
    @Query("select m from Member m join fetch m.postList where m.id = :id")
    Optional<Member> findMemberWithPost(@Param("id") Long id);

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
    @Query("select m from Member m join fetch m.roles where m.email = :email and m.username = :username")
    boolean existsByEmailAndUsername(@Param("email") String email, @Param("username") String username);
    @Query("select m from Member m join fetch m.roles where m.email = :email and m.username = :username and m.loginId = :loginId")
    boolean existsByEmailAndUsernameAndLoginId(@Param("email") String email, @Param("username") String username,@Param("loginId") String loginId);

    Optional<Member> findMemberByEmail(String email);
}
