package durikkiri.project.controller;

import durikkiri.project.entity.dto.FindDto;
import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.security.JwtToken;
import durikkiri.project.service.MailService;
import durikkiri.project.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MailService mailService;

    @PostMapping("/sign-up")
    public ResponseEntity<Map<String , String>> signUp(@Valid @RequestBody SignUpDto signUpDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        memberService.signUp(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Member signed up successfully"));
    }

    @GetMapping("/exists/loginId")
    public ResponseEntity<Boolean> checkLoginIdDuplicate(@RequestParam String loginId) {
        return ResponseEntity.ok(memberService.checkLoginIdDuplicate(loginId));
    }

    @GetMapping("/exists/nickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam String nickname) {
        return ResponseEntity.ok(memberService.checkNicknameDuplicate(nickname));
    }

    @PostMapping("/exists/email")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        // 이메일로 인증 코드 전송 로직 (MailService 사용)
        mailService.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }

    // 이메일 인증 코드 확인
    @PostMapping("/exists/code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean verified = mailService.verifyCode(email, code);
        return ResponseEntity.ok(Collections.singletonMap("verified", verified));
    }

    @PostMapping("/find")
    public ResponseEntity<?> sendVerificationCodeToFind(@RequestBody FindDto findDto) {
        mailService.existsMemberSendVerificationCode(findDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/exists/id")
    public ResponseEntity<?> findLoginId(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean verified = mailService.verifyCode(email, code);
        if (!verified) {
            throw new BadRequestException("인증 코드가 유효하지 않습니다.");
        }
        String loginId = memberService.findLoginIdByEmailAndUsername(email);
        return ResponseEntity.ok(Collections.singletonMap("loginId", loginId));
    }

    @PostMapping("/find/password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");

        boolean verified = mailService.verifyCode(email, code);
        if (!verified) {
            throw new BadRequestException("인증 코드가 유효하지 않습니다.");
        }
        memberService.changePassword(email, newPassword);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/sign-in")
    public ResponseEntity<JwtToken> signIn(@Valid @RequestBody SignInDto signInDto) {

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
    public ResponseEntity<Map<String , String>>  updateMember(@Valid @RequestBody MemberUpdateDto memberUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        memberService.updateMember(memberUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("message", "Member updated successfully"));
    }

    @DeleteMapping("/member")
    public ResponseEntity<String> deleteMember() {
        memberService.deleteMember();
        return ResponseEntity.ok("Member deleted successfully");
    }
    private Map<String, String> getErrorMap(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}
