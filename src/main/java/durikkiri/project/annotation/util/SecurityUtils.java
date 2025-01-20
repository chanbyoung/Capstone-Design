package durikkiri.project.annotation.util;

import durikkiri.project.exception.AuthenticationException;
import durikkiri.project.security.CustomUserDetails;

public class SecurityUtils {
    private static final Long GUEST_USER_ID = -1L;

    public static Long checkAuthenticationPrincipal(Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return GUEST_USER_ID;
        }
        if (!(principal instanceof CustomUserDetails)) {
            throw new AuthenticationException("Invalid principal type: " + principal.getClass().getName());
        }
        return Long.valueOf(((CustomUserDetails) principal).getUsername());
    }
}
