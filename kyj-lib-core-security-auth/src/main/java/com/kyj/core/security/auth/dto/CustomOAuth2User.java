package com.kyj.core.security.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

/**
 * 2025-09-21
 * @author 김용준
 * OAuth2 사용자 정보를 담는 커스텀 UserDetails 구현체
 */
public class CustomOAuth2User implements OAuth2User {

    private final AuthMemberDTO authMemberDTO;
    private final boolean isOAuth2Login;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(AuthMemberDTO authMemberDTO, boolean isOAuth2Login) {
        this.authMemberDTO = authMemberDTO;
        this.isOAuth2Login = isOAuth2Login;
        this.attributes = new HashMap<>();
    }

    public CustomOAuth2User(AuthMemberDTO authMemberDTO, boolean isOAuth2Login, Map<String, Object> attributes) {
        this.authMemberDTO = authMemberDTO;
        this.isOAuth2Login = isOAuth2Login;
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authMemberDTO.getRole()));
        return authorities;
    }

    @Override
    public String getName() {
        return authMemberDTO.getUsername();
    }

    /**
     * 회원 정보 반환
     */
    public AuthMemberDTO getAuthMemberDTO() {
        return authMemberDTO;
    }

    /**
     * OAuth2 로그인 여부 반환
     */
    public boolean isOAuth2Login() {
        return isOAuth2Login;
    }

    /**
     * 사용자 ID 반환
     */
    public Long getUserId() {
        return authMemberDTO.getUserId();
    }

    /**
     * 사용자명 반환
     */
    public String getUsername() {
        return authMemberDTO.getUsername();
    }

    /**
     * 이메일 반환
     */
    public String getEmail() {
        return authMemberDTO.getEmail();
    }

    /**
     * 역할 정보 반환
     */
    public String getRole() {
        return authMemberDTO.getRole();
    }
}