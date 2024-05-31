package durikkiri.project.controller;

import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.security.JwtToken;
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
