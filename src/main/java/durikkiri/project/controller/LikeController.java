package durikkiri.project.controller;

import durikkiri.project.annotation.AuthUser;
import durikkiri.project.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;
    @PostMapping("/{postId}")
    public ResponseEntity<String> addLike(
            @AuthUser Long memberId,
            @PathVariable Long postId) {
        likeService.toggleLike(postId, memberId);
        return new ResponseEntity<>("Like add successfully", HttpStatus.OK);
    }
}
