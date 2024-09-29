package durikkiri.project.security;

import durikkiri.project.entity.Member;
import durikkiri.project.exception.ForbiddenException;
import durikkiri.project.repository.MemberRepository;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            MemberRepository memberRepository,
                            RedisTemplate<String, Object> redisTemplate) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 1800000); // 30 minutes
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(now + 86400000)) // 24 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        //redis에 refreshToken 저장
        saveRefreshTokenWithAuth(authentication.getName(), refreshToken, authorities);

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    // Redis에 Refresh Token과 권한 정보 함께 저장
    private void saveRefreshTokenWithAuth(String loginId, String refreshToken, String authorities) {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            HashMap<String, Object> map = new HashMap<>();
            map.put("refreshToken", refreshToken);
            map.put("authorities", authorities);
            hashOperations.putAll(loginId, map);

            // TTL 설정
            redisTemplate.expire(loginId, 1, TimeUnit.DAYS); // 24시간 유효
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure", e);
        } catch (RedisSystemException e) {
            log.error("Redis system exception", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while saving to Redis", e);
        }
    }

    // Refresh Token을 사용하여 새로운 Access Token을 생성하는 메서드
    public JwtToken refreshAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new ForbiddenException("Invalid refresh token");
        }

        String loginId = getUsernameFromToken(refreshToken);

        String storedRefreshToken = (String) redisTemplate.opsForHash().get(loginId,"refreshToken");
        String authorities = (String) redisTemplate.opsForHash().get(loginId, "authorities");
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new ForbiddenException("Invalid refresh token");
        }

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ForbiddenException("유저 이름이 없습니다."));
        long now = (new Date()).getTime();

        log.info("memberLoginId = "+member.getLoginId() + " loginId" + loginId);
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 1800000); // 30 minutes
        String newAccessToken = Jwts.builder()
                .setSubject(loginId)
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Use the same refresh token
                .build();
    }
    // 토큰에서 사용자 이름을 추출하는 메서드
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
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

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
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


    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public long getExpiration(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.getTime();
    }
}
