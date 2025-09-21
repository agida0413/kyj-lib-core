package com.kyj.core.security.auth.service;

import com.kyj.core.security.auth.dto.AuthMemberDTO;
import com.kyj.core.security.auth.dto.oauth2.OAuth2Response;

import java.util.Map;

/**
 * 2025-09-21
 * @author 김용준
 * 실제 구현 서버에서 구현해야 하는 회원 관리 서비스 인터페이스 (템플릿 메서드 패턴)
 * 이 인터페이스를 구현하여 데이터베이스 접근, JWT 추가 정보 등을 커스터마이징
 */
public interface AuthMemberService {

    /**
     * OAuth2 사용자 정보로 회원 조회 또는 생성
     * @param oAuth2Response OAuth2 제공자 응답
     * @return 회원 정보
     */
    AuthMemberDTO findOrCreateMember(OAuth2Response oAuth2Response);

    /**
     * 사용자 ID로 회원 조회
     * @param userId 사용자 ID
     * @return 회원 정보 (없으면 null)
     */
    AuthMemberDTO findMemberByUserId(String userId);

    /**
     * 사용자명으로 회원 조회
     * @param username 사용자명
     * @return 회원 정보 (없으면 null)
     */
    AuthMemberDTO findMemberByUsername(String username);

    /**
     * JWT 토큰에 추가할 커스텀 클레임 생성
     * @param memberDTO 회원 정보
     * @return 추가 클레임 맵
     */
    default Map<String, Object> createCustomClaims(AuthMemberDTO memberDTO) {
        // 기본 구현은 빈 맵 반환
        return Map.of();
    }

    /**
     * 회원 정보 업데이트 (마지막 로그인 시간 등)
     * @param memberDTO 회원 정보
     */
    default void updateMemberLoginInfo(AuthMemberDTO memberDTO) {
        // 기본 구현은 아무것도 하지 않음
    }

    /**
     * 회원 활성 상태 확인
     * @param memberDTO 회원 정보
     * @return 활성 상태 여부
     */
    default boolean isMemberActive(AuthMemberDTO memberDTO) {
        return memberDTO.isActive();
    }

    /**
     * 로그아웃 시 추가 처리
     * @param memberDTO 회원 정보
     */
    default void onMemberLogout(AuthMemberDTO memberDTO) {
        // 기본 구현은 아무것도 하지 않음
    }
}