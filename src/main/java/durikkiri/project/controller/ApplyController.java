package durikkiri.project.controller;

import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apply")
@RequiredArgsConstructor
public class ApplyController {
    private final ApplyService applyService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> addApply(@PathVariable Long postId, @RequestBody ApplyAddDto applyAddDto) {
        return new ResponseEntity<>(applyService.addApply(postId, applyAddDto));
    }
}
