package com.kyj.fmk.core.cst.enm;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
/**
 * 2025-05-29
 * @author 김용준
 * Restful Api에서 사용하는 에러응답 Enum
 */
@Getter
public enum ApiErrCode {

    CM001("CM001","기본응답메시지 {0}"),
    CM002("CM002","서버내부 오류입니다."),
    CM003("CM003","쿠키추출 오류입니다.");

    private final String code;
    private final String msg;

    /**
     * 에러코드 생성자
     * @param code
     * @param msg
     */
    ApiErrCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 정적 맵 초기화 (O(1) 조회용)
     */
    private static final Map<String, ApiErrCode> CODE_MAP = new HashMap<>();

    static {
        for (ApiErrCode errCode : values()) {
            CODE_MAP.put(errCode.code, errCode);
        }
    }

    /**
     * O1복잡도로 정적 맵에서 알맞은 메시지를 가져온다.
     * @param code
     * @return ApiErrCode
     */
    public static String of(String code) {
        return CODE_MAP.get(code).getMsg();
    }



}
