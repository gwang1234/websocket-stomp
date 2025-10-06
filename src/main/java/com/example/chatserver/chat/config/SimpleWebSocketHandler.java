package com.example.chatserver.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// connect로 웹소켓 연결 요청이 들어왔을때, 이를 처리할 클래스
@Component
@Slf4j
public class SimpleWebSocketHandler extends TextWebSocketHandler {

    // 연결된 세션 관리: thread safe한 set 사용
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    // 연결되고난 후 처리 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("Connected : " + session.getId());
    }

    // 사용자한테 메세지를 보내주는 메서드
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("recieved message: " + payload);
        for (WebSocketSession s: sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    // 연결이 끊기면
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("disconnected!");
    }

}
