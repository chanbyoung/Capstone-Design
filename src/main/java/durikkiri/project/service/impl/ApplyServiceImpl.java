package durikkiri.project.service.impl;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.apply.AppliesGetsDto;
import durikkiri.project.controller.ApplyUpdateDto;
import durikkiri.project.entity.Apply;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.repository.ApplyRepository;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.entity.dto.apply.ApplyGetDto;
import durikkiri.project.service.ApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static durikkiri.project.entity.ApplyStatus.*;
import static org.springframework.http.HttpStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ApplyServiceImpl implements ApplyService {
    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;


    @Override
    public List<AppliesGetsDto> getApplies() {
         return applyRepository.findApply().stream().map(AppliesGetsDto::toDto).toList();
    }

    @Override
    @Transactional
    public HttpStatus addApply(Long postId, ApplyAddDto applyAddDto) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> findMember = memberRepository.findByLoginId(memberLoginId);

        Optional<Post> findPost = postRepository.findPostWithField(postId);
        if (findPost.isPresent() && findMember.isPresent()) {
            Apply apply = applyAddDto.toEntity(findPost.get(), findMember.get());
            if (apply != null) {
                applyRepository.save(apply);
                return OK;
            }
        }
        return NOT_FOUND;
    }

    @Override
    @Transactional
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
                .orElseThrow(() -> new IllegalArgumentException("Apply not found"));
        log.info("{}", apply.toString());
        if (applyStatus.equals(ACCEPT)) {
            apply.postFieldUpdate();
            apply.getPost().updateStatus();
        }else {
            apply.updateStatus(applyStatus);
        }
    }

    @Override
    @Transactional
    public HttpStatus updateApply(Long applyId, ApplyUpdateDto applyUpdateDto) {
        Optional<Apply> findApply = applyRepository.findById(applyId);
        if (findApply.isPresent()) {
            Apply apply = findApply.get();
            apply.updateContent(applyUpdateDto);
            return OK;
        }
        return NOT_FOUND;
    }

    @Override
    @Transactional
    public HttpStatus deleteApply(Long applyId) {
        Optional<Apply> findApply = applyRepository.findById(applyId);
        if (findApply.isPresent()) {
            applyRepository.delete(findApply.get());
            return OK;
        }
        return NOT_FOUND;
    }
}
