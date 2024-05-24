package durikkiri.project.controller;

import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.security.JwtToken;
import durikkiri.project.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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
        memberService.signUp(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Member signed up successfully");
    }

    @GetMapping("/exists/loginId/{loginId}")
    public ResponseEntity<Boolean> checkLoginIdDuplicate(@PathVariable String loginId) {
        return ResponseEntity.ok(memberService.checkLoginIdDuplicate(loginId));
    }

    @GetMapping("/exists/nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.checkNicknameDuplicate(nickname));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtToken> signIn(@RequestBody SignInDto signInDto) {
        log.info("{} {}", signInDto.getLoginId(), signInDto.getPassword());
        JwtToken jwtToken = memberService.signIn(signInDto);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String jwtToken = resolveToken(request);
        if (jwtToken != null) {
            memberService.logout(jwtToken);
        }
        return ResponseEntity.noContent().build();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberGetDto> getMember(@PathVariable Long memberId) {
        MemberGetDto member = memberService.getMember(memberId);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/member")
    public ResponseEntity<MemberGetDto> getMyInfo() {
        MemberGetDto member = memberService.getMyInfo();
        return ResponseEntity.ok(member);
    }

    @PatchMapping("/member")
    public ResponseEntity<String> updateMember(@RequestBody MemberUpdateDto memberUpdateDto) {
        memberService.updateMember(memberUpdateDto);
        return ResponseEntity.ok("Member updated successfully");
    }

    @DeleteMapping("/member")
    public ResponseEntity<String> deleteMember() {
        memberService.deleteMember();
        return ResponseEntity.ok("Member deleted successfully");
    }
}
