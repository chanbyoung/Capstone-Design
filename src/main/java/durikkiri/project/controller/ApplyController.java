package durikkiri.project.controller;

import durikkiri.project.annotation.AuthUser;
import durikkiri.project.dto.apply.AppliesGetsDto;
import durikkiri.project.dto.apply.ApplyAddDto;
import durikkiri.project.dto.apply.ApplyGetDto;
import durikkiri.project.dto.apply.ApplyPostDto;
import durikkiri.project.dto.apply.ApplyUpdateDto;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.service.ApplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applies")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;

    @GetMapping
    public ResponseEntity<List<AppliesGetsDto>> getApplies(@AuthUser Long memberId) {
        List<AppliesGetsDto> applies = applyService.getApplies(memberId);
        return ResponseEntity.ok(applies);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AppliesGetsDto>> getMyApplies(@AuthUser Long memberId) {
        return ResponseEntity.ok(applyService.getMyApplies(memberId));
    }

    @PostMapping("/apply/{postId}")
    public ResponseEntity<Map<String, String>> addApply(@PathVariable Long postId,
            @Valid @RequestBody ApplyAddDto applyAddDto,
            @AuthUser Long memberId,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        applyService.addApply(postId, applyAddDto, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "Apply created successfully"));
    }

    @GetMapping("/{applyId}")
    public ResponseEntity<ApplyGetDto> getApply(@PathVariable Long applyId) {
        ApplyGetDto apply = applyService.getApply(applyId);
        return ResponseEntity.ok(apply);
    }

    @PostMapping("/{applyId}")
    public ResponseEntity<Map<String, String>> acceptApply(@PathVariable Long applyId,
            @Valid @RequestBody ApplyPostDto applyPostDto,
            @AuthUser Long memberId,
            BindingResult bindingResult) {
        applyService.updateApplyStatus(applyId, applyPostDto.getApplyStatus(), memberId);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("message", "Apply status updated successfully"));
    }

    //지원이 수락된 지원서를 취소하는 메서드
    @PatchMapping("/{applyId}/cancel")
    public ResponseEntity<String> cancelApply(@PathVariable Long applyId, @AuthUser Long memberId) {
        applyService.updateApplyStatus(applyId, ApplyStatus.REJECT, memberId);
        return ResponseEntity.ok("Apply cancel successfully");
    }

    @PatchMapping("/{applyId}")
    public ResponseEntity<Map<String, String>> updateApply(@PathVariable Long applyId,
            @Valid @RequestBody ApplyUpdateDto applyUpdateDto,
            @AuthUser Long memberId,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        applyService.updateApply(applyId, applyUpdateDto, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "Apply updated successfully"));
    }

    @DeleteMapping("/{applyId}")
    public ResponseEntity<String> deleteApply(@PathVariable Long applyId,
            @AuthUser Long memberId) {
        applyService.deleteApply(applyId, memberId);
        return ResponseEntity.ok("Apply deleted successfully");
    }

    private Map<String, String> getErrorMap(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}
