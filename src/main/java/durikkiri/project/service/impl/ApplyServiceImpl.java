package durikkiri.project.service.impl;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.apply.AppliesGetsDto;
import durikkiri.project.entity.dto.apply.ApplyUpdateDto;
import durikkiri.project.entity.Apply;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.exception.*;
import durikkiri.project.repository.ApplyRepository;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.entity.dto.apply.ApplyGetDto;
import durikkiri.project.service.ApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static durikkiri.project.entity.ApplyStatus.*;

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
    public void addApply(Long postId, ApplyAddDto applyAddDto) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        Post post = postRepository.findPostWithField(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        Apply apply = applyAddDto.toEntity(post, member);
        applyRepository.save(apply);
    }

    @Override
    @Transactional
    public ApplyGetDto getApply(Long applyId) {
        Apply apply = applyRepository.findById(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));

        if (apply.getApplyStatus().equals(UNREAD)) {
            apply.updateStatus(READ);
        }
        return ApplyGetDto.toDto(apply);
    }

    @Override
    @Transactional
    public void acceptApply(Long applyId, ApplyStatus applyStatus) {
        Apply apply = applyRepository.findApplyWithPost(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));
        log.info("{}", apply.toString());
        if (applyStatus.equals(ACCEPT)) {
            apply.postFieldUpdate();
            apply.getPost().updateStatus();
        } else {
            apply.updateStatus(applyStatus);
        }
    }

    @Override
    @Transactional
    public void updateApply(Long applyId, ApplyUpdateDto applyUpdateDto) {
        Apply apply = applyRepository.findById(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));
        apply.updateContent(applyUpdateDto);
    }

    @Override
    @Transactional
    public void deleteApply(Long applyId) {
        Apply apply = applyRepository.findById(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));
        applyRepository.delete(apply);
    }
}
