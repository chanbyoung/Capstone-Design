package durikkiri.project.service;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.security.JwtToken;
import org.springframework.http.HttpStatus;

public interface MemberService {
    HttpStatus signUp(SignUpDto signUpDto);
    JwtToken signIn(SignInDto signInDto);
    MemberGetDto getMyInfo();

    MemberGetDto getMember(Long memberId);
}
