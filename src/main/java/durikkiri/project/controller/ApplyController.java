package durikkiri.project.controller;

import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.entity.dto.apply.ApplyGetDto;
import durikkiri.project.entity.dto.apply.ApplyPostDto;
import durikkiri.project.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apply")
@RequiredArgsConstructor
public class ApplyController {
    private final ApplyService applyService;
    @PostMapping("/{postId}/apply")
    public ResponseEntity<String> addApply(@PathVariable Long postId, @RequestBody ApplyAddDto applyAddDto) {
        return new ResponseEntity<>(applyService.addApply(postId, applyAddDto));
    }

    @GetMapping("/{applyId}")
    public ResponseEntity<ApplyGetDto> getApply(@PathVariable Long applyId) {
        ApplyGetDto apply = applyService.getApply(applyId);
        if (apply != null) {
            return new ResponseEntity<>(apply, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{applyId}")
    public ResponseEntity<Void> acceptApply(@PathVariable Long applyId, @RequestBody ApplyPostDto applyPostDto) {
        applyService.acceptApply(applyId, applyPostDto.getApplyStatus());
        return ResponseEntity.ok().build(); // 성공 시 200 OK 상태 코드 반환
    }

}
