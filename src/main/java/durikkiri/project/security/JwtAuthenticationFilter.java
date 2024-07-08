package durikkiri.project.security;

import durikkiri.project.exception.AuthenticationException;
import durikkiri.project.exception.ForbiddenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // 1. Request Header에서 JWT 토큰 추출
            String token = resolveToken((HttpServletRequest) request);

            // 2. validateToken 으로 토큰 유효성 검사
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 블랙리스트에 있는지 확인
                Boolean isBlacklisted = redisTemplate.hasKey(token);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    throw new ForbiddenException("Token is blacklisted");
                }

                // 토큰이 유효하고 블랙리스트에 없을 경우 Authentication 객체를 가져와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (ForbiddenException ex) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
