package durikkiri.project.controller;

import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.security.JwtToken;
import durikkiri.project.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpDto) {
        HttpStatus httpStatus = memberService.signUp(signUpDto);
        return new ResponseEntity<>(httpStatus);
    }

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        log.info("{} {}",signInDto.getLoginId(),signInDto.getPassword());
        JwtToken jwtToken = memberService.signIn(signInDto);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberGetDto> getMember(@PathVariable Long memberId) {
        MemberGetDto member = memberService.getMember(memberId);
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @GetMapping("/member")
    public ResponseEntity<MemberGetDto> getMyInfo() {
        MemberGetDto member = memberService.getMyInfo();
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }
}
