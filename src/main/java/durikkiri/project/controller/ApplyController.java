package durikkiri.project.controller;

import durikkiri.project.entity.dto.apply.*;
import durikkiri.project.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

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

    @PostMapping("/apply/{postId}")
    public ResponseEntity<String> addApply(@PathVariable Long postId, @RequestBody ApplyAddDto applyAddDto) {
        applyService.addApply(postId, applyAddDto);
        return ResponseEntity.status(CREATED).body("Apply created successfully");
    }

    @GetMapping("/{applyId}")
    public ResponseEntity<ApplyGetDto> getApply(@PathVariable Long applyId) {
        ApplyGetDto apply = applyService.getApply(applyId);
        return ResponseEntity.ok(apply);
    }

    @PostMapping("/{applyId}")
    public ResponseEntity<String> acceptApply(@PathVariable Long applyId, @RequestBody ApplyPostDto applyPostDto) {
        applyService.acceptApply(applyId, applyPostDto.getApplyStatus());
        return ResponseEntity.ok("Apply status updated successfully");
    }

    @PatchMapping("/{applyId}")
    public ResponseEntity<String> updateApply(@PathVariable Long applyId, @RequestBody ApplyUpdateDto applyUpdateDto) {
        applyService.updateApply(applyId, applyUpdateDto);
        return ResponseEntity.ok("Apply updated successfully");
    }

    @DeleteMapping("/{applyId}")
    public ResponseEntity<String> deleteApply(@PathVariable Long applyId) {
        applyService.deleteApply(applyId);
        return ResponseEntity.ok("Apply deleted successfully");
    }
}
