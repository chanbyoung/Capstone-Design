package durikkiri.project.security;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.auth.RefreshTokenInfoDto;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.exception.ForbiddenException;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.repository.RedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30;
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;
    private final Key key;
    private final RedisRepository redisRepository;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            RedisRepository redisRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisRepository = redisRepository;
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String nickName = ((CustomUserDetails) authentication.getPrincipal()).getNickName();

        // Access, Refresh Token 생성
        String accessToken = createToken(authentication.getName(), roles, ACCESS_TOKEN_EXPIRATION, nickName);
        String refreshToken = createToken(authentication.getName(), null, REFRESH_TOKEN_EXPIRATION, null);

        RefreshTokenInfoDto refreshTokenInfoDto = new RefreshTokenInfoDto(authentication.getName(),
                refreshToken, roles, nickName);

        //redis에 refreshToken 저장
        redisRepository.storeRefreshToken(refreshTokenInfoDto);

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 공통 토큰 생성 로직
    private String createToken(String userId, String roles, long expiration, String nickname) {
        Claims claims = Jwts.claims().setSubject(userId);
        if (roles != null) {
            claims.put("roles", roles);
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token을 사용하여 새로운 Access Token을 생성하는 메서드
    /**
     * Refresh 토큰을 이용한 Access 토큰 갱신
     */
    public JwtToken refreshAccessToken(String refreshToken) {
        // 1. 토큰 유효성 검사
        validateToken(refreshToken);

        // 2. 토큰에서 사용자 ID 추출
        String userId = getMemberIdFromToken(refreshToken);

        // 3. Redis에서 Refresh 토큰 유효성 검사
        if (!redisRepository.isValidRefreshToken(userId, refreshToken)) {
            throw new BadRequestException("유효하지 않은 토큰입니다.");
        }

        // 4. Redis에서 사용자 권한 정보 추출
        String authorities = redisRepository.getAuthorities(userId);
        String nickName = redisRepository.getNickName(userId);

        // 5. 새로운 Access 토큰 생성
        String newAccessToken = createToken(userId, authorities, ACCESS_TOKEN_EXPIRATION, nickName);

        // 7. 새로운 AuthResponseDto 반환 (기존 Refresh 토큰 유지)
        return new JwtToken(newAccessToken, refreshToken);
    }

    // 토큰에서 사용자 이름을 추출하는 메서드
    public String getMemberIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String authority = authorities.isEmpty() ? "ROLE_USER" : authorities.iterator().next().getAuthority();
        String nickName = (String) claims.get("nickName");
        log.info("authentication nickName ={} ",nickName);

        CustomUserDetails principal = CustomUserDetails.builder()
                .username(claims.getSubject())
                .nickName(nickName)
                .authority(authority)            // 첫 번째 권한 설정
                .build();
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에서 만료 시간 가져오기
     *
     * @param token JWT 토큰
     * @return 토큰의 남은 만료 시간 (밀리초)
     */
    public long getExpiration(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
