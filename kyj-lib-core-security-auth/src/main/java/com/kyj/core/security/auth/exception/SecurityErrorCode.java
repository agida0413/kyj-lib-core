package com.kyj.core.security.auth.exception;

import lombok.Getter;

/**
 * 2025-09-21
 * @author 김용준
 * 인증 서버에서 사용하는 보안 에러 코드 Enum
 */
@Getter
public enum SecurityErrorCode {

    // 토큰 관련 에러 (SEC001 ~ SEC099)
    SEC001("SEC001", "액세스 토큰이 없습니다."),
    SEC002("SEC002", "잘못된 토큰 형식입니다."),
    SEC003("SEC003", "만료된 토큰입니다."),
    SEC004("SEC004", "토큰 생성에 실패했습니다."),
    SEC005("SEC005", "토큰 검증에 실패했습니다."),
    SEC006("SEC006", "리프레시 토큰이 없습니다."),
    SEC007("SEC007", "세션이 만료되었습니다."),
    SEC008("SEC008", "토큰이 블랙리스트에 등록되었습니다."),

    // 인증 관련 에러 (SEC100 ~ SEC199)
    SEC010("SEC010", "인증되지 않은 요청입니다."),
    SEC011("SEC011", "허가되지 않은 요청입니다."),
    SEC012("SEC012", "OAuth2 인증에 실패했습니다."),
    SEC013("SEC013", "사용자 정보를 찾을 수 없습니다."),
    SEC014("SEC014", "로그인이 필요합니다."),
    SEC015("SEC015", "권한이 부족합니다."),

    // 회원 관련 에러 (SEC200 ~ SEC299)
    SEC020("SEC020", "회원 가입에 실패했습니다."),
    SEC021("SEC021", "이미 존재하는 회원입니다."),
    SEC022("SEC022", "비활성화된 계정입니다."),
    SEC023("SEC023", "탈퇴된 계정입니다."),

    // Redis 관련 에러 (SEC300 ~ SEC399)
    SEC030("SEC030", "Redis 연결에 실패했습니다."),
    SEC031("SEC031", "토큰 저장에 실패했습니다."),
    SEC032("SEC032", "토큰 삭제에 실패했습니다.");

    private final String code;
    private final String message;

    SecurityErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}