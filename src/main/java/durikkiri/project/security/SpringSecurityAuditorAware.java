package durikkiri.project.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
@Slf4j
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(null == authentication || !authentication.isAuthenticated()) {
            return null;
        }

        String userId = authentication.getName();
        log.info("{}", userId);
        return Optional.of(userId);
    }
}
