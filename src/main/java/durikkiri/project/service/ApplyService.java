package durikkiri.project.service;

import durikkiri.project.entity.dto.apply.AppliesGetsDto;
import durikkiri.project.entity.dto.apply.ApplyUpdateDto;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.entity.dto.apply.ApplyGetDto;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface ApplyService {
    List<AppliesGetsDto> getApplies();

    HttpStatus addApply(Long postId, ApplyAddDto applyAddDto);

    ApplyGetDto getApply(Long applyId);

    void acceptApply(Long applyId, ApplyStatus applyStatus);

    HttpStatus updateApply(Long applyId, ApplyUpdateDto applyUpdateDto);

    HttpStatus deleteApply(Long applyId);

}
