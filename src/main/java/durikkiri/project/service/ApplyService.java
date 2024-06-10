package durikkiri.project.service;

import durikkiri.project.entity.dto.apply.AppliesGetsDto;
import durikkiri.project.entity.dto.apply.ApplyUpdateDto;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.entity.dto.apply.ApplyGetDto;

import java.util.List;

public interface ApplyService {
    List<AppliesGetsDto> getApplies();
    List<AppliesGetsDto> getMyApplies();
    void addApply(Long postId, ApplyAddDto applyAddDto);

    ApplyGetDto getApply(Long applyId);

    void acceptApply(Long applyId, ApplyStatus applyStatus);

    void updateApply(Long applyId, ApplyUpdateDto applyUpdateDto);

    void deleteApply(Long applyId);

}
