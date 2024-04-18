package durikkiri.project.service;

import durikkiri.project.controller.ApplyUpdateDto;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.entity.dto.apply.ApplyGetDto;
import org.springframework.http.HttpStatus;

public interface ApplyService {
    HttpStatus addApply(Long postId, ApplyAddDto applyAddDto);

    ApplyGetDto getApply(Long applyId);

    void acceptApply(Long applyId, ApplyStatus applyStatus);

    HttpStatus updateApply(Long applyId, ApplyUpdateDto applyUpdateDto);

    HttpStatus deleteApply(Long applyId);
}
