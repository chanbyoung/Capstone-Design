package durikkiri.project.service;

import durikkiri.project.entity.dto.apply.ApplyAddDto;
import org.springframework.http.HttpStatus;

public interface ApplyService {
    HttpStatus addApply(Long postId, ApplyAddDto applyAddDto);
}
