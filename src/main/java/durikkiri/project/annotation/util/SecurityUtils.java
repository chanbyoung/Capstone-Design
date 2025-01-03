package durikkiri.project.annotation.util;

import durikkiri.project.exception.AuthenticationException;
import durikkiri.project.security.CustomUserDetails;

public class SecurityUtils {
    public static Object checkAuthenticationPrincipal(Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return CustomUserDetails.createAnonymousUser();
        }
        if (!(principal instanceof CustomUserDetails)) {
            throw new AuthenticationException("Invalid principal type: " + principal.getClass().getName());
        }
        return principal;
    }
}
