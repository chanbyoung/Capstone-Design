package durikkiri.project.service;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.security.JwtToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public interface MemberService {
    HttpStatus signUp(SignUpDto signUpDto);
    JwtToken signIn(SignInDto signInDto);
    MemberGetDto getMyInfo();

    MemberGetDto getMember(Long memberId);

    HttpStatus updateMember(MemberUpdateDto memberUpdateDto);

    HttpStatus deleteMember();

    Boolean checkLoginIdDuplicate(String loginId);

    Boolean checkNicknameDuplicate(String nickname);


}
