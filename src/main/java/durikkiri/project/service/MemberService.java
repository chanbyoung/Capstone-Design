package durikkiri.project.service;

import durikkiri.project.entity.dto.auth.ExistDto;
import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.security.JwtToken;

public interface MemberService {
    void signUp(SignUpDto signUpDto);
    JwtToken signIn(SignInDto signInDto);
    MemberGetDto getMyInfo(Long memberId);

    MemberGetDto getMember(String nickname);

    void updateMember(MemberUpdateDto memberUpdateDto, Long memberId);

    void deleteMember(Long memberId);

    String findLoginIdByEmailAndUsername(String email);
    void changePassword(ExistDto existDto);

    Boolean checkLoginIdDuplicate(String loginId);

    Boolean checkNicknameDuplicate(String nickname);


    void logout(String jwtToken);

    JwtToken refreshAccessToken(String refreshToken);
}
