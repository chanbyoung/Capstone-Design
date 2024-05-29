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
    @Query("select a from Apply a join fetch a.post join fetch a.post.fieldList where a.id = :id")
    Optional<Apply> findApplyWithPost(@Param("id") Long id);
    @Query("select a from Apply a join fetch a.post p")
    //join fetch p.member where p.member.id = :memberId
    //내가 작성한 게시글의 지원한 지원자들의 게시글 조회
    List<Apply> findApply();

    // 특정 게시물에 대해 특정 회원의 신청이 존재하는지 확인
    boolean existsByPostAndMember(Post post, Member member);

}
