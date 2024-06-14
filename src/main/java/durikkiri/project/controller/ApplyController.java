package durikkiri.project.controller;

import durikkiri.project.entity.dto.apply.*;
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
    public ResponseEntity<List<AppliesGetsDto>> getApplies() {
        List<AppliesGetsDto> applies = applyService.getApplies();
        return ResponseEntity.ok(applies);
    }
    @GetMapping("/my")
    public ResponseEntity<List<AppliesGetsDto>> getMyApplies() {
        return ResponseEntity.ok(applyService.getMyApplies());
    }

    @PostMapping("/apply/{postId}")
    public ResponseEntity<Map<String, String>> addApply(@PathVariable Long postId, @Valid @RequestBody ApplyAddDto applyAddDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        applyService.addApply(postId, applyAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Apply created successfully"));
    }

    @GetMapping("/{applyId}")
    public ResponseEntity<ApplyGetDto> getApply(@PathVariable Long applyId) {
        ApplyGetDto apply = applyService.getApply(applyId);
        return ResponseEntity.ok(apply);
    }

    @PostMapping("/{applyId}")
    public ResponseEntity<Map<String, String>> acceptApply(@PathVariable Long applyId,@Valid @RequestBody ApplyPostDto applyPostDto,
                                              BindingResult bindingResult) {
        applyService.acceptApply(applyId, applyPostDto.getApplyStatus());
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("message", "Apply status updated successfully"));
    }

    @PatchMapping("/{applyId}")
    public ResponseEntity<Map<String, String>> updateApply(@PathVariable Long applyId,@Valid @RequestBody ApplyUpdateDto applyUpdateDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        applyService.updateApply(applyId, applyUpdateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Apply updated successfully"));
    }

    @DeleteMapping("/{applyId}")
    public ResponseEntity<String> deleteApply(@PathVariable Long applyId) {
        applyService.deleteApply(applyId);
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
