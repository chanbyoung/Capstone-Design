package durikkiri.project.repository;

import durikkiri.project.entity.Conversation;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.post.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("select c from Conversation c where ((c.member1 = :member1 and c.member2 = :member2) or (c.member1 = :member2 and c.member2 = :member1)) and c.post = :post")
    Optional<Conversation> findByMember1OrMember2(@Param("member1") Member member1, @Param("member2") Member member2, @Param("post") Post post);

    @Query("select c from Conversation c join fetch c.member1 join fetch c.member2 where c.member1 =:member or c.member2 =:member")
    List<Conversation> findByConversation(@Param("member") Member member);
    @Query("SELECT DISTINCT c FROM Conversation c " +
            "JOIN FETCH c.member1 m1 " +
            "JOIN FETCH c.member2 m2 " +
            "LEFT JOIN FETCH c.messages msg " +
            "LEFT JOIN FETCH msg.sender " +
            "WHERE c.id = :conversationId " +
            "AND (m1.id = :memberId OR m2.id = :memberId)")
    Optional<Conversation> findByIdWithMessage(@Param("conversationId") Long conversationId, @Param("memberId") Long memberId);
}
