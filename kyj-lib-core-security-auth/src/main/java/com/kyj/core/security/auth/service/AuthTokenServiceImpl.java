package com.kyj.core.security.auth.service;

import com.kyj.core.security.auth.constants.RedisKeys;
import com.kyj.core.security.auth.dto.AuthMemberDTO;
import com.kyj.core.security.auth.exception.SecurityErrorCode;
import com.kyj.core.security.auth.exception.SecurityException;
import com.kyj.core.security.auth.jwt.AuthJWTUtil;
import com.kyj.core.security.auth.util.SecurityResponseUtil;
import com.kyj.core.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AuthJWTUtil authJWTUtil;
    private final AuthMemberService authMemberService;

    @Override
    public void addRefreshToken(String userId, String token) {
        try {
            String key = RedisKeys.refreshTokenKey(userId);
            redisTemplate.opsForValue().set(key, token, 24, TimeUnit.HOURS);
            log.debug("리프레시 토큰 저장: userId={}", userId);
        } catch (Exception e) {
            log.error("리프레시 토큰 저장 실패: userId={}, error={}", userId, e.getMessage());
            throw new SecurityException(SecurityErrorCode.SEC031, "토큰 저장에 실패했습니다.");
        }
    }

    @Override
    public void deleteRefreshToken(String userId, String token) {
        try {
            String key = RedisKeys.refreshTokenKey(userId);
            redisTemplate.delete(key);
            log.debug("리프레시 토큰 삭제: userId={}", userId);
        } catch (Exception e) {
            log.error("리프레시 토큰 삭제 실패: userId={}, error={}", userId, e.getMessage());
            throw new SecurityException(SecurityErrorCode.SEC032, "토큰 삭제에 실패했습니다.");
        }
    }

    @Override
    public boolean isRefreshTokenExists(String userId, String token) {
        try {
            String key = RedisKeys.refreshTokenKey(userId);
            String storedToken = redisTemplate.opsForValue().get(key);
            return token.equals(storedToken);
        } catch (Exception e) {
            log.error("리프레시 토큰 확인 실패: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public void addToBlacklist(String token) {
        try {
            String key = RedisKeys.blacklistTokenKey(token);
            redisTemplate.opsForValue().set(key, "blacklisted", 24, TimeUnit.HOURS);
            log.debug("토큰 블랙리스트 등록");
        } catch (Exception e) {
            log.error("토큰 블랙리스트 등록 실패: error={}", e.getMessage());
            throw new SecurityException(SecurityErrorCode.SEC031, "토큰 저장에 실패했습니다.");
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = RedisKeys.blacklistTokenKey(token);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("토큰 블랙리스트 확인 실패: error={}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = CookieUtil.getCookieValue(request, "refresh");

            if (!StringUtils.hasText(refreshToken)) {
                SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC006);
                return false;
            }

            if (!authJWTUtil.validate(refreshToken)) {
                SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC002);
                return false;
            }

            String category = authJWTUtil.getCategory(refreshToken);
            if (!"refresh".equals(category)) {
                SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC002);
                return false;
            }

            String userId = authJWTUtil.getUserId(refreshToken);
            if (!isRefreshTokenExists(userId, refreshToken)) {
                SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC007);
                return false;
            }

            AuthMemberDTO memberDTO = authMemberService.findMemberByUserId(userId);
            if (memberDTO == null || !authMemberService.isMemberActive(memberDTO)) {
                SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC013);
                return false;
            }

            String newAccessToken = authJWTUtil.createJwt("access", memberDTO.getUsername(),
                String.valueOf(memberDTO.getUserId()), memberDTO.getEmail(),
                memberDTO.getRole(), 3600000L);

            String newRefreshToken = authJWTUtil.createJwt("refresh", memberDTO.getUsername(),
                String.valueOf(memberDTO.getUserId()), memberDTO.getEmail(),
                memberDTO.getRole(), 86400000L);

            deleteRefreshToken(userId, refreshToken);
            addRefreshToken(userId, newRefreshToken);

            CookieUtil.addCookie(response, "Authorization", newAccessToken, 3600);
            CookieUtil.addCookie(response, "refresh", newRefreshToken, 86400);

            log.info("토큰 재발급 성공: userId={}", userId);
            return true;

        } catch (Exception e) {
            log.error("토큰 재발급 실패: {}", e.getMessage());
            SecurityResponseUtil.writeErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, SecurityErrorCode.SEC050);
            return false;
        }
    }

    @Override
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accessToken = CookieUtil.getCookieValue(request, "Authorization");
            String refreshToken = CookieUtil.getCookieValue(request, "refresh");

            if (StringUtils.hasText(accessToken)) {
                addToBlacklist(accessToken);
                String userId = authJWTUtil.getUserId(accessToken);

                if (StringUtils.hasText(refreshToken)) {
                    deleteRefreshToken(userId, refreshToken);
                }

                AuthMemberDTO memberDTO = authMemberService.findMemberByUserId(userId);
                if (memberDTO != null) {
                    authMemberService.onMemberLogout(memberDTO);
                }
            }

            CookieUtil.deleteCookie(response, "Authorization");
            CookieUtil.deleteCookie(response, "refresh");

            log.info("로그아웃 성공");
            return true;

        } catch (Exception e) {
            log.error("로그아웃 실패: {}", e.getMessage());
            SecurityResponseUtil.writeErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, SecurityErrorCode.SEC050);
            return false;
        }
    }

    @Override
    public void deleteAllRefreshTokensByUser(String userId) {
        try {
            String key = RedisKeys.refreshTokenKey(userId);
            redisTemplate.delete(key);
            log.debug("사용자의 모든 리프레시 토큰 삭제: userId={}", userId);
        } catch (Exception e) {
            log.error("사용자 리프레시 토큰 삭제 실패: userId={}, error={}", userId, e.getMessage());
            throw new SecurityException(SecurityErrorCode.SEC032, "토큰 삭제에 실패했습니다.");
        }
    }
}