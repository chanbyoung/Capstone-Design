package durikkiri.project.controller;

import durikkiri.project.entity.dto.post.*;
import durikkiri.project.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostsGetDto>> getPosts(@PageableDefault Pageable pageable, @RequestBody(required = false) PostSearchContent postSearchContent) {
        Page<PostsGetDto> posts = postService.getPosts(pageable, postSearchContent);
        return ResponseEntity.ok(posts);
    }
    @PostMapping
    public ResponseEntity<String> addPost(@Valid @RequestBody PostAddDto postAddDto) {
        return new ResponseEntity<>(postService.addPost(postAddDto));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostGetDto> getPost(@PathVariable Long postId) {
        PostGetDto post = postService.getPost(postId);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<String> updatePost(@RequestBody PostUpdateDto postUpdateDto, @PathVariable Long postId) {
        return new ResponseEntity<>(postService.updatePost(postId, postUpdateDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.deletePost(postId));
    }

}
