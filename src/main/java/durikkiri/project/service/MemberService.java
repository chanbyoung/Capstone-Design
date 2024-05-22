package durikkiri.project.service;

import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.security.JwtToken;

public interface MemberService {
    void signUp(SignUpDto signUpDto);
    JwtToken signIn(SignInDto signInDto);
    MemberGetDto getMyInfo();

    MemberGetDto getMember(Long memberId);

    void updateMember(MemberUpdateDto memberUpdateDto);

    void deleteMember();

    Boolean checkLoginIdDuplicate(String loginId);

    Boolean checkNicknameDuplicate(String nickname);


}
