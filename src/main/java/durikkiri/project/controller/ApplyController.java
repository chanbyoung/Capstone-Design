package durikkiri.project.controller;

import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.entity.dto.apply.ApplyGetDto;
import durikkiri.project.entity.dto.apply.ApplyPostDto;
import durikkiri.project.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/applies")
@RequiredArgsConstructor
public class ApplyController {
    private final ApplyService applyService;
    @PostMapping("/apply/{postId}")
    public ResponseEntity<String> addApply(@PathVariable Long postId, @RequestBody ApplyAddDto applyAddDto) {
        return new ResponseEntity<>(applyService.addApply(postId, applyAddDto));
    }

    @GetMapping("/{applyId}")
    public ResponseEntity<ApplyGetDto> getApply(@PathVariable Long applyId) {
        ApplyGetDto apply = applyService.getApply(applyId);
        if (apply != null) {
            return new ResponseEntity<>(apply, OK);
        }
        return new ResponseEntity<>(NOT_FOUND);
    }

    @PostMapping("/{applyId}")
    public ResponseEntity<String> acceptApply(@PathVariable Long applyId, @RequestBody ApplyPostDto applyPostDto) {
        try {
            applyService.acceptApply(applyId, applyPostDto.getApplyStatus());
            return ResponseEntity.ok().build(); // 성공 시 200 OK 상태 코드 반환
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @PatchMapping("/{applyId}")
    public ResponseEntity<String> updateApply(@PathVariable Long applyId, @RequestBody ApplyUpdateDto applyUpdateDto) {
        HttpStatus httpStatus = applyService.updateApply(applyId, applyUpdateDto);
        if (httpStatus.equals(OK)) {
            return new ResponseEntity<>(httpStatus);
        }
        return new ResponseEntity<>("지원서를 찾을 수 없습니다.", NOT_FOUND);
    }

    @DeleteMapping("/{applyId}")
    public ResponseEntity<String> deleteApply(@PathVariable Long applyId) {
        HttpStatus httpStatus = applyService.deleteApply(applyId);
        if (httpStatus.equals(OK)) {
            return new ResponseEntity<>(httpStatus);
        }
        return new ResponseEntity<>("지원서를 찾을 수 없습니다.", NOT_FOUND);
    }

}
