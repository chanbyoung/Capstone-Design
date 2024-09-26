package durikkiri.project.controller;

import durikkiri.project.entity.dto.comment.CommentDto;
import durikkiri.project.entity.dto.post.*;
import durikkiri.project.entity.post.Category;
import durikkiri.project.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class PostController {

    private final PostService postService;
    private static final String VIEWED_COOKIE_PREFIX = "viewed_";
    private static final int COOKIE_EXPIRE_SECONDS = 24 * 60 * 60; // 24 hours

    @GetMapping
    public ResponseEntity<Page<PostsGetDto>> getPosts(@PageableDefault Pageable pageable,
                                                      @RequestParam Category category,
                                                      @RequestParam(required = false) String title) {
        log.info("category = {}", category);
        Page<PostsGetDto> posts = postService.getPosts(pageable, new PostSearchContent(category, title));
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addPost(@Valid @RequestPart(value = "json") PostAddDto postAddDto,
                                          @RequestPart(value = "image", required = false) MultipartFile image,
                                          BindingResult bindingResult
                                          ) throws IOException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        postService.addPost(postAddDto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Post created successfully"));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostGetDto> getPost(@PathVariable Long postId,
                                              HttpServletRequest request, HttpServletResponse response) {
        Cookie viewCookie = findViewCookie(postId, request);
        boolean shouldIncreaseViewCount = (viewCookie == null);
        if (shouldIncreaseViewCount) {
            addViewCountCookie(postId, response);
        }
        PostGetDto post = postService.getPost(postId, shouldIncreaseViewCount);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    private void addViewCountCookie(Long postId, HttpServletResponse response) {
        Cookie newCookie = new Cookie(VIEWED_COOKIE_PREFIX + postId, "true");
        newCookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
        newCookie.setPath("/");
        response.addCookie(newCookie);
    }

    private static Cookie findViewCookie(Long postId, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(VIEWED_COOKIE_PREFIX + postId)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Map<String, String>> updatePost(@Valid @RequestPart(value = "json") PostUpdateDto postUpdateDto,
                                             @RequestPart(value = "image", required = false) MultipartFile image,
                                             @PathVariable Long postId,
                                             BindingResult bindingResult
                                             ) throws IOException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorMap(bindingResult));
        }
        postService.updatePost(postId, image, postUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("message", "Post updated successfully"));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<String> addComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        postService.addComment(postId, commentDto);
        return new ResponseEntity<>("Comment added successfully", HttpStatus.CREATED);
    }

    @PatchMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        postService.updateComment(commentId, commentDto);
        return new ResponseEntity<>("Comment updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }


    private Map<String, String> getErrorMap(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}
