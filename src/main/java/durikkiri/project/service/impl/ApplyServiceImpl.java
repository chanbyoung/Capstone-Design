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
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        return applyRepository.findApply(member.getId()).stream().map(AppliesGetsDto::toDto).toList();
    }

    @Override
    public List<AppliesGetsDto> getMyApplies() {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        return applyRepository.findMyApply(member).stream().map(AppliesGetsDto::toDto).toList();
    }

    @Override
    @Transactional
    public void addApply(Long postId, ApplyAddDto applyAddDto) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        Post post = postRepository.findPostWithField(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        // post의 작성자인 member와 현재 로그인한 member가 동일한지 확인
        if (post.getMember().equals(member)) {
            throw new BadRequestException("You cannot apply to your own post");
        }
        //중복 신청 방지
        boolean alreadyApplied = applyRepository.existsByPostAndMember(post, member);
        if (alreadyApplied) {
            throw new BadRequestException("You have already applied to this post");
        }

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
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        // 게시물 작성자 검증
        if (!apply.getPost().getMember().getLoginId().equals(memberLoginId)) {
            throw new ForbiddenException("User not authorized to accept or reject this apply");
        }
        if (applyStatus.equals(ACCEPT)) {
            if (apply.getApplyStatus().equals(ACCEPT)) {
                throw new BadRequestException("이미 수락처리된 지원서입니다");
            }
            apply.updateStatus(ACCEPT);
            apply.postFieldUpdate(true);
            apply.getPost().updateStatus();
        } else {
            apply.updateStatus(applyStatus);
        }
    }

    @Override
    @Transactional
    public void cancelApply(Long applyId) {
        Apply apply = applyRepository.findApplyWithPost(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        // 게시물 작성자 검증
        if (!apply.getPost().getMember().getLoginId().equals(memberLoginId)) {
            throw new ForbiddenException("User not authorized to accept or reject this apply");
        }
        if (apply.getApplyStatus().equals(REJECT)) {
            throw new BadRequestException("이미 취소처리된 지원서입니다.");
        }
        apply.postFieldUpdate(false);
        apply.updateStatus(REJECT);
        apply.getPost().updateStatus();
    }

    @Override
    @Transactional
    public void updateApply(Long applyId, ApplyUpdateDto applyUpdateDto) {
        Apply apply = applyRepository.findById(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 신청자 검증
        if (!apply.getMember().getLoginId().equals(memberLoginId)) {
            throw new ForbiddenException("User not authorized to update this apply");
        }
        if (apply.getApplyStatus().equals(ACCEPT) || apply.getApplyStatus().equals(REJECT)) {
            throw new BadRequestException("해당 지원서가 이미 수락/반려 처리되어 수정 할 수 없습니다.");
        }
        apply.updateContent(applyUpdateDto);
    }

    @Override
    @Transactional
    public void deleteApply(Long applyId) {
        Apply apply = applyRepository.findById(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
    // 신청자 검증
        if (!apply.getMember().getLoginId().equals(memberLoginId)) {
            throw new ForbiddenException("User not authorized to delete this apply");
        }
        applyRepository.delete(apply);
    }
}
