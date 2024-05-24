package durikkiri.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        log.error("NotFoundException: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // BadRequestException과 RecruitmentException을 하나의 핸들러에서 처리
    @ExceptionHandler({BadRequestException.class, RecruitmentException.class})
    public ResponseEntity<String> handleBadRequestExceptions(RuntimeException ex) {
        log.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<String> handleForbiddenException(ForbiddenException ex) {
        log.error("ForbiddenException: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        // 오류 발생 시 좀 더 친절한 메시지를 제공하는 것이 좋습니다.
        return new ResponseEntity<>("서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        log.error("AuthenticationException: {}", ex.getMessage());
        // 사용자가 인증 실패 상황에서 더 명확한 지침을 받을 수 있도록 메시지를 개선합니다.
        return new ResponseEntity<>("인증에 실패하였습니다. 로그인 정보를 확인해주세요.", HttpStatus.UNAUTHORIZED);
    }
}
