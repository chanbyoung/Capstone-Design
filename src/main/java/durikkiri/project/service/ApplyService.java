package durikkiri.project.service;

import durikkiri.project.dto.apply.AppliesGetsDto;
import durikkiri.project.dto.apply.ApplyUpdateDto;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.dto.apply.ApplyAddDto;
import durikkiri.project.dto.apply.ApplyGetDto;

import java.util.List;

public interface ApplyService {

    List<AppliesGetsDto> getApplies(Long memberId);

    List<AppliesGetsDto> getMyApplies(Long memberId);

    void addApply(Long postId, ApplyAddDto applyAddDto, Long memberId);

    ApplyGetDto getApply(Long applyId);

    void updateApplyStatus(Long applyId, ApplyStatus applyStatus, Long memberId);

    void updateApply(Long applyId, ApplyUpdateDto applyUpdateDto, Long memberId);

    void deleteApply(Long applyId, Long memberId);

}