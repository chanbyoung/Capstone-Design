package durikkiri.project.entity.dto.message;

import durikkiri.project.entity.Conversation;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.member.MemberDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ConversationGetsDto {
    private Long id;
    private MemberDto counterpartMember;
    private LocalDateTime recentMessage;

    static public ConversationGetsDto toDto(Conversation conversation, Member currentUser) {
        Member counterpartMember;
        // 대화에 포함된 회원 중에서 현재 회원과 상대방 회원을 식별하여 저장
        if (conversation.getMember1().equals(currentUser)) {
            counterpartMember = conversation.getMember2();
        } else {
            counterpartMember = conversation.getMember1();
        }

        return ConversationGetsDto.builder()
                .id(conversation.getId())
                .counterpartMember(MemberDto.toDto(counterpartMember))
                .recentMessage(conversation.getModifiedAt())
                .build();
    }

}
