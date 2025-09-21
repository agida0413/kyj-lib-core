package com.kyj.core.security.auth.service;

import com.kyj.core.security.auth.dto.AuthMemberDTO;
import com.kyj.core.security.auth.dto.CustomOAuth2User;
import com.kyj.core.security.auth.dto.oauth2.GoogleResponse;
import com.kyj.core.security.auth.dto.oauth2.KakaoResponse;
import com.kyj.core.security.auth.dto.oauth2.NaverResponse;
import com.kyj.core.security.auth.dto.oauth2.OAuth2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * 2025-09-21
 * @author 김용준
 * OAuth2 사용자 정보를 처리하는 커스텀 서비스 (템플릿 메서드 패턴 적용)
 * AuthMemberService를 통해 실제 구현 서버에서 회원 관리 로직을 커스터마이징 가능
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthMemberService authMemberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("OAuth2 로그인 요청: {}", userRequest.getClientRegistration().getRegistrationId());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = createOAuth2Response(registrationId, oAuth2User.getAttributes());

        if (oAuth2Response == null) {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }

        // 템플릿 메서드 패턴: 실제 구현 서버에서 AuthMemberService를 통해 회원 관리
        AuthMemberDTO memberDTO = authMemberService.findOrCreateMember(oAuth2Response);

        // 회원 활성 상태 확인
        if (!authMemberService.isMemberActive(memberDTO)) {
            throw new OAuth2AuthenticationException("비활성화된 계정입니다: " + memberDTO.getUsername());
        }

        // 로그인 정보 업데이트 (마지막 로그인 시간 등)
        authMemberService.updateMemberLoginInfo(memberDTO);

        return new CustomOAuth2User(memberDTO, true, oAuth2User.getAttributes());
    }

    /**
     * OAuth2 제공자별 응답 객체 생성
     */
    private OAuth2Response createOAuth2Response(String registrationId, java.util.Map<String, Object> attributes) {
        switch (registrationId) {
            case "google":
                return new GoogleResponse(attributes);
            case "naver":
                return new NaverResponse(attributes);
            case "kakao":
                return new KakaoResponse(attributes);
            default:
                return null;
        }
    }

}