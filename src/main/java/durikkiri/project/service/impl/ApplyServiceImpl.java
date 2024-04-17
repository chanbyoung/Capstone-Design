package durikkiri.project.service.impl;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.Post;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.repository.ApplyRepository;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.entity.dto.apply.ApplyGetDto;
import durikkiri.project.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static durikkiri.project.entity.ApplyStatus.*;
import static org.springframework.http.HttpStatus.*;

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
                return OK;
            }
        }
        return NOT_FOUND;
    }

    @Override
    public ApplyGetDto getApply(Long applyId) {
        Optional<Apply> findApply = applyRepository.findById(applyId);
        if (findApply.isPresent()) {
            Apply apply = findApply.get();
            if (apply.getApplyStatus().equals(UNREAD)) {
                apply.updateStatus(READ);
            }
            return ApplyGetDto.toDto(apply);
        }
        return null;
    }

    @Override
    @Transactional
    public void acceptApply(Long applyId, ApplyStatus applyStatus) {
        Apply apply = applyRepository.findApplyWithPost(applyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apply not found"));

        if (applyStatus.equals(ACCEPT)) {
            HttpStatus status = apply.postFieldUpdate();
            if (!status.equals(HttpStatus.OK)) {
                throw new ResponseStatusException(status, "Failed to update field");
            }
        } else {
            apply.updateStatus(applyStatus);
        }
    }
}
