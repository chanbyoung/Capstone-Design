package durikkiri.project.entity.dto.member;

import durikkiri.project.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberGetDto {
    private Long id;
    private String username;
    private String nickname;

    static public MemberGetDto toDto(Member member) {
        return MemberGetDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }
}
