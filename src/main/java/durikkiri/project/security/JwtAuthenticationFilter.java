package durikkiri.project.security;

import durikkiri.project.exception.AuthenticationException;
import durikkiri.project.exception.ForbiddenException;
import io.jsonwebtoken.ExpiredJwtException;
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

    private static final String REFRESH_ENDPOINT = "/api/members/refresh";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        if (REFRESH_ENDPOINT.equals(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 1. Request Header에서 JWT 토큰 추출
            String token = resolveToken(httpRequest);

            // 2. validateToken 으로 토큰 유효성 검사
            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    // 토큰이 블랙리스트에 있는지 확인
                    Boolean isBlacklisted = redisTemplate.hasKey(token);
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        throw new ForbiddenException("Token is blacklisted");
                    }

                    // 토큰이 유효하고 블랙리스트에 없을 경우 Authentication 객체를 가져와서 SecurityContext에 저장
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new AuthenticationException("Token validation failed");
                }
            }
        } catch (ExpiredJwtException ex) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).getWriter().write("Token expired");
            return;
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).getWriter().write("Authentication failed");
            return;
        } catch (ForbiddenException ex) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            ((HttpServletResponse) response).getWriter().write("Token is blacklisted");
            return;
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ((HttpServletResponse) response).getWriter().write("Internal server error");
            return;
        }
        chain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }
}
