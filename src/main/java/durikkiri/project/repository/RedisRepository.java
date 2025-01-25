package durikkiri.project.repository;

import durikkiri.project.entity.dto.auth.RefreshTokenInfoDto;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * Refresh 토큰과 사용자 정보를 Redis에 저장
     */
    public void storeRefreshToken(RefreshTokenInfoDto tokenData) {
        try {
            // 기존 데이터 삭제
            redisTemplate.delete(tokenData.getAccount());

            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(tokenData.getAccount(), createTokenDataMap(tokenData));
            redisTemplate.expire(tokenData.getAccount(), 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis에 Refresh Token 저장 실패: {}", e.getMessage());
        }
    }

    /**
     * Refresh 토큰이 유효한지 확인
     */
    public boolean isValidRefreshToken(String userAccount, String refreshToken) {
        try {
            String storedRefreshToken = (String) redisTemplate.opsForHash()
                    .get(userAccount, "refreshToken");
            return storedRefreshToken.equals(refreshToken);
        } catch (Exception e) {
            log.warn("Redis에서 Refresh Token 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 로그아웃 시 액세스 토큰과 리프레시 토큰을 블랙리스트에 추가
     */
    public void logoutTokens(String jwtToken, long accessTokenExpiration, String userId) {
        try {
            redisTemplate.opsForValue().set(
                    jwtToken,
                    "blacklisted",
                    accessTokenExpiration,
                    TimeUnit.MILLISECONDS);
            redisTemplate.delete(userId);
        } catch (Exception e) {
            log.warn("Redis에서 로그아웃 처리 실패: {}", e.getMessage());
        }
    }

    /**
     * 사용자 권한 정보 가져오기
     */
    public String getAuthorities(String userAccount) {
        try {
            return (String) redisTemplate.opsForHash().get(userAccount, "authorities");
        } catch (Exception e) {
            log.warn("Redis에서 권한 정보 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 사용자 권한 정보 가져오기
     */
    public String getNickName(String userAccount) {
        try {
            return (String) redisTemplate.opsForHash().get(userAccount, "nickname");
        } catch (Exception e) {
            log.warn("Redis에서 회원 닉네임 정보 조회 실패: {}", e.getMessage());
            return null;
        }
    }


    private HashMap<String, Object> createTokenDataMap(RefreshTokenInfoDto tokenData) {
        HashMap<String, Object> tokenDataMap = new HashMap<>();
        tokenDataMap.put("refreshToken", tokenData.getRefreshToken());
        tokenDataMap.put("authorities", tokenData.getAuthorities());
        tokenDataMap.put("nickname", tokenData.getNickName());
        return tokenDataMap;
    }



}
