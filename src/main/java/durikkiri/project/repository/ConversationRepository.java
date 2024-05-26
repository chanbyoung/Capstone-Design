package durikkiri.project.repository;

import durikkiri.project.entity.Conversation;
import durikkiri.project.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("select c from Conversation c where (c.member1 = :member1 and c.member2 = :member2) or (c.member1 = :member2 and c.member2 = :member1)")
    Optional<Conversation> findByMember1OrMember2(@Param("member1") Member member1, @Param("member2") Member member2);

    @Query("select c from Conversation c join fetch c.member1 join fetch c.member2 where c.member1 =:member or c.member2 =:member")
    List<Conversation> findByConversation(@Param("member") Member member);
    @Query("select c from Conversation c join fetch c.messages m join fetch m.sender where c.id = :conversationId")
    Optional<Conversation> findByIdWithMessage(@Param("conversationId") Long conversationId);
}
