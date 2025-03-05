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
    public List<AppliesGetsDto> getApplies(Long memberId) {
        return applyRepository.findApply(memberId)
                .stream()
                .map(AppliesGetsDto::toDto)
                .toList();
    }

    @Override
    public List<AppliesGetsDto> getMyApplies(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        return applyRepository.findMyApply(member)
                .stream()
                .map(AppliesGetsDto::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void addApply(Long postId, ApplyAddDto applyAddDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        Post post = postRepository.findPostWithField(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        valid(post, member);

        Apply apply = applyAddDto.toEntity(post, member);
        applyRepository.save(apply);
    }

    private void valid(Post post, Member member) {
        // post의 작성자인 member와 현재 로그인한 member가 동일한지 확인
        if (post.getMember().equals(member)) {
            throw new BadRequestException("You cannot apply to your own post");
        }
        if (post.getRecruitmentInfo().getStatus().equals("closed")) {
            throw new BadRequestException("이미 모집이 완료된 게시글입니다.");
        }
        //중복 신청 방지
//        boolean alreadyApplied = applyRepository.existsByPostAndMember(post, member);
//        if (alreadyApplied) {
//            throw new BadRequestException("You have already applied to this post");
//        }
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
    public void updateApplyStatus(Long applyId, ApplyStatus applyStatus, Long memberId) {
        Apply apply = applyRepository.findApplyWithPost(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));
        // 게시물 작성자 검증
        if (!apply.getRecruitmentInfo().getPost().getMember().getId().equals(memberId)) {
            throw new ForbiddenException("User not authorized to accept or reject this apply");
        }

        // 이미 처리된 상태인지 검증
        if (apply.getApplyStatus().equals(applyStatus)) {
            throw new BadRequestException(String.format("이미 %s 처리된 지원서입니다", applyStatus));
        }

        // 상태 업데이트
        apply.updateStatus(applyStatus);

        // ACCEPT 또는 REJECT일 경우 추가 처리
        if (applyStatus.equals(ACCEPT) || applyStatus.equals(REJECT)) {
            apply.postFieldUpdate(applyStatus.equals(ACCEPT));
            apply.getRecruitmentInfo().updateStatus();
        }
    }

    @Override
    @Transactional
    public void updateApply(Long applyId, ApplyUpdateDto applyUpdateDto, Long memberId) {
        Apply apply = applyRepository.findById(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));

        // 신청자 검증
        if (!apply.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("User not authorized to update this apply");
        }
        if (apply.getApplyStatus().equals(ACCEPT) || apply.getApplyStatus().equals(REJECT)) {
            throw new BadRequestException("해당 지원서가 이미 수락/반려 처리되어 수정 할 수 없습니다.");
        }
        apply.updateContent(applyUpdateDto);
    }

    @Override
    @Transactional
    public void deleteApply(Long applyId, Long memberId) {
        Apply apply = applyRepository.findById(applyId)
                .orElseThrow(() -> new NotFoundException("Apply not found"));
        // 신청자 검증
        if (!apply.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("User not authorized to delete this apply");
        }
        applyRepository.delete(apply);
    }
}
