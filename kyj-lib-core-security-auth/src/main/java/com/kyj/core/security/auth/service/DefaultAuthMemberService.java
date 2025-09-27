package com.kyj.core.security.auth.service;

import com.kyj.core.security.auth.dto.AuthMemberDTO;
import com.kyj.core.security.auth.dto.oauth2.OAuth2Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 2025-09-21
 * @author 김용준
 * 기본 회원 서비스 구현체 (개발용/테스트용)
 * 실제 서비스에서는 AuthMemberService를 구현하여 이 빈을 대체해야 함
 */
@Slf4j
@Service
@ConditionalOnProperty(
        prefix = "kyj.security.auth",
        name = "use-default-member-service",
        havingValue = "true",
        matchIfMissing = true
)
public class DefaultAuthMemberService implements AuthMemberService {

    @Override
    public AuthMemberDTO findOrCreateMember(OAuth2Response oAuth2Response) {
        log.warn("기본 AuthMemberService 사용 중 - 실제 구현으로 대체하세요!");

        // 임시 구현 - 실제로는 데이터베이스 연동 필요
        AuthMemberDTO memberDTO = new AuthMemberDTO();
        memberDTO.setUserId(1L); // 임시 사용자 ID
        memberDTO.setUsername(oAuth2Response.getEmail());
        memberDTO.setEmail(oAuth2Response.getEmail());
//        memberDTO.setNickname(oAuth2Response.getNickname());
        memberDTO.setRole("ROLE_USER");
        memberDTO.setProvider(oAuth2Response.getProvider());
        memberDTO.setProviderId(oAuth2Response.getProviderId());
        memberDTO.setActive(true);

        log.info("기본 회원 정보 생성: {}", memberDTO.getEmail());
        return memberDTO;
    }

    @Override
    public AuthMemberDTO findMemberByUserId(String userId) {
        log.warn("기본 AuthMemberService.findMemberByUserId 사용 중 - 실제 구현으로 대체하세요!");
        return null;
    }

    @Override
    public AuthMemberDTO findMemberByUsername(String username) {
        log.warn("기본 AuthMemberService.findMemberByUsername 사용 중 - 실제 구현으로 대체하세요!");
        return null;
    }
}