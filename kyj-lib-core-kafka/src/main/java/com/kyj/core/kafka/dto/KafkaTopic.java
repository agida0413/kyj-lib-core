package com.kyj.core.kafka.dto;
/**
 * 2025-08-29
 * @author 김용준
 * KAFKA 토픽 리스트
 */
public interface KafkaTopic {


    //로그아웃 시
    public static final String MEMBER_LOGOUT = "member.logout";


    //웹소켓 푸시 이벤트 목록 들
    public static final String REALTIME_PUSH_WS_DISCONNECT = "realtime.push-ws-disconnect";


}
