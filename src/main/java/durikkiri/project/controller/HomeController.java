package durikkiri.project.controller;

import durikkiri.project.entity.dto.HomeGetDto;
import durikkiri.project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/homes")
public class HomeController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<HomeGetDto>> getHome() {
        List<HomeGetDto> home = postService.getHome();
        return ResponseEntity.ok(home);
    }
}
