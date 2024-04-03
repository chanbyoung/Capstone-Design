package durikkiri.project.controller;

import durikkiri.project.entity.dto.post.*;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class PostController {

    private final PostService postService;
    private static final String VIEWED_COOKIE_PREFIX = "viewed_";
    private static final int COOKIE_EXPIRE_SECONDS = 24 * 60 * 60; // 24시간

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
    public ResponseEntity<PostGetDto> getPost(@PathVariable Long postId,
                                              HttpServletRequest request, HttpServletResponse response) {
        Cookie viewCookie = findViewCookie(postId, request);
        boolean shouldIncreaseViewCount = (viewCookie == null);
        if (shouldIncreaseViewCount) {
            addViewCountCookie(postId, response);
        }
        PostGetDto post = postService.getPost(postId, shouldIncreaseViewCount);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
    public ResponseEntity<String> updatePost(@RequestBody PostUpdateDto postUpdateDto, @PathVariable Long postId) {
        return new ResponseEntity<>(postService.updatePost(postId, postUpdateDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.deletePost(postId));
    }

}
