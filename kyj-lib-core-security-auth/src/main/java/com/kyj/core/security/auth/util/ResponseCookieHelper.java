package com.kyj.core.security.auth.util;

import com.kyj.core.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

/**
 * 2025-09-21
 * @author 김용준
 * HttpServletResponse에 쿠키를 설정하는 헬퍼 클래스
 */
public final class ResponseCookieHelper {

    private ResponseCookieHelper() {}

    /**
     * HttpServletResponse에 쿠키 추가
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        ResponseCookie cookie = CookieUtil.createCookie(name, value, maxAgeSeconds, "/");
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * HttpServletResponse에서 쿠키 삭제
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = CookieUtil.deleteCookie(name, "/");
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}