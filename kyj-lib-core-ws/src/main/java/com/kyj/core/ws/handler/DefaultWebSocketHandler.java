package com.kyj.core.ws.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * 2025-09-24
 * @author 김용준
 * 기본 웹소켓 메시지 핸들러
 * 기본적인 웹소켓 메시지 처리를 제공
 */
@Slf4j
@Controller
public class DefaultWebSocketHandler {

    /**
     * 기본 메시지 처리
     */
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Object handleMessage(Object message) {
        log.debug("메시지 수신: {}", message);

        // 기본적으로 받은 메시지를 그대로 브로드캐스트
        return processMessage(message);
    }

    /**
     * 구독 시 처리
     */
    @SubscribeMapping("/topic/status")
    public String handleSubscription() {
        log.debug("상태 토픽 구독");
        return "연결됨";
    }

    /**
     * 에코 메시지 처리
     */
    @MessageMapping("/echo")
    @SendTo("/queue/echo")
    public Object handleEcho(Object message) {
        log.debug("에코 메시지: {}", message);
        return message;
    }

    /**
     * 메시지 처리 로직 (확장 가능)
     */
    protected Object processMessage(Object message) {
        // 기본 구현은 메시지를 그대로 반환
        // 하위 클래스에서 오버라이드하여 사용자 정의 처리 로직 구현 가능
        return message;
    }
}