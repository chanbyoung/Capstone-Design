package durikkiri.project.repository;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
    @Query("select a from Apply a join fetch a.recruitmentInfo r join fetch r.fieldList join fetch r.post.member where a.id = :id")
    Optional<Apply> findApplyWithPost(@Param("id") Long id);
    @Query("select a from Apply a join fetch a.recruitmentInfo r join fetch r.post p where p.member.id = :memberId")
    List<Apply> findApply(@Param("memberId") Long id);

    @Query("select a from Apply a join fetch a.recruitmentInfo r join fetch a.member m where m = :member")
    List<Apply> findMyApply(@Param("member") Member member);

    @Query("""
           SELECT a
           FROM Apply a
           JOIN FETCH a.recruitmentInfo r
           JOIN FETCH r.post p
           JOIN FETCH p.member m
           WHERE a.id = :apply_id
           """)
    Optional<Apply> findApplyWithRecruitmentById(@Param("apply_id") Long applyId);
}
