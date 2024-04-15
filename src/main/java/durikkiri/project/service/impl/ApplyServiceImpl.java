package durikkiri.project.service.impl;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.Field;
import durikkiri.project.entity.Post;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.repository.ApplyRepository;
import durikkiri.project.repository.DslPostRepository;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {
    private final ApplyRepository applyRepository;
    private final PostRepository postRepository;
    @Override
    @Transactional
    public HttpStatus addApply(Long postId, ApplyAddDto applyAddDto) {
        Optional<Post> findPost = postRepository.findPostWithField(postId);
        if (findPost.isPresent()) {
            Apply apply = applyAddDto.toEntity(findPost.get());
            if (apply != null) {
                applyRepository.save(apply);
                return HttpStatus.OK;
            }
        }
        return HttpStatus.NOT_FOUND;
    }
}
