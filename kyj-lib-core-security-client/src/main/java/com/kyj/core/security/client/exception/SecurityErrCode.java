package com.kyj.core.security.client.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 2025-09-21
 * @author 김용준
 * 시큐리티 클라이언트에서 사용되는 에러코드 집합 (kyj-lib-core CmErrCode 포맷 준수)
 */
@Getter
public enum SecurityErrCode {

    // 토큰 관련 에러 (SEC001 ~ SEC099)
    SEC001("SEC001", "액세스 토큰이 없습니다."),
    SEC002("SEC002", "잘못된 토큰 형식입니다."),
    SEC003("SEC003", "만료된 토큰입니다."),
    SEC004("SEC004", "토큰 검증에 실패했습니다."),
    SEC005("SEC005", "토큰이 블랙리스트에 등록되었습니다."),
    SEC006("SEC006", "리프레시 토큰이 없습니다."),
    SEC007("SEC007", "세션이 만료되었습니다."),
    SEC008("SEC008", "Authorization 헤더가 없거나 형식이 잘못되었습니다."),

    // 인증 관련 에러 (SEC010 ~ SEC019)
    SEC010("SEC010", "인증되지 않은 요청입니다."),
    SEC011("SEC011", "허가되지 않은 요청입니다."),
    SEC012("SEC012", "인증 정보가 유효하지 않습니다."),
    SEC013("SEC013", "사용자 정보를 찾을 수 없습니다."),

    // 권한 관련 에러 (SEC020 ~ SEC029)
    SEC020("SEC020", "접근 권한이 없습니다."),
    SEC021("SEC021", "필요한 권한이 부족합니다."),
    SEC022("SEC022", "관리자 권한이 필요합니다."),

    // AWS API Gateway 관련 에러 (SEC030 ~ SEC039)
    SEC030("SEC030", "API Gateway 헤더가 누락되었습니다."),
    SEC031("SEC031", "API Gateway에서 전달된 사용자 정보가 유효하지 않습니다."),
    SEC032("SEC032", "API Gateway 인증 정보 파싱 실패입니다."),

    // 블랙리스트 관련 에러 (SEC040 ~ SEC049)
    SEC040("SEC040", "블랙리스트에 등록된 토큰입니다."),
    SEC041("SEC041", "블랙리스트에 등록된 사용자입니다."),
    SEC042("SEC042", "차단된 계정입니다."),

    // 시스템 관련 에러 (SEC050 ~ SEC059)
    SEC050("SEC050", "보안 모듈 내부 오류입니다."),
    SEC051("SEC051", "Redis 연결 오류입니다."),
    SEC052("SEC052", "보안 설정 오류입니다."),
    SEC053("SEC053", "블랙리스트 조회 중 오류가 발생했습니다.");

    private final String code;
    private final String message;

    /**
     * 에러코드 생성자
     * @param code 에러코드
     * @param message 에러메시지
     */
    SecurityErrCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 정적 맵 초기화 (O(1) 조회용)
     */
    private static final Map<String, SecurityErrCode> CODE_MAP = new HashMap<>();

    static {
        for (SecurityErrCode errCode : values()) {
            CODE_MAP.put(errCode.code, errCode);
        }
    }

    /**
     * 에러코드로 SecurityErrCode 조회
     * @param code 에러코드
     * @return SecurityErrCode (없으면 null)
     */
    public static SecurityErrCode of(String code) {
        return CODE_MAP.get(code);
    }

    /**
     * 에러코드로 메시지 조회
     * @param code 에러코드
     * @return 에러메시지 (없으면 null)
     */
    public static String getMessageByCode(String code) {
        SecurityErrCode errCode = CODE_MAP.get(code);
        return errCode != null ? errCode.getMessage() : null;
    }
}