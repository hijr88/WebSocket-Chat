package me.yh.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.yh.chatroom.model.ChatMessage;
import me.yh.chatroom.model.ChatRoom;
import me.yh.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatRoomRepository repository;

    @Autowired
    public ChatHandler(ObjectMapper objectMapper, ChatRoomRepository chatRoomRepository) {
        this.objectMapper = objectMapper;
        this.repository = chatRoomRepository;
    }

    /**
     * //connection이 맺어진 후 실행된다.
     * @param session 채팅방 유저의 세션
     */
    @Override 
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("=== afterConnectionEstablished ===");
        if (session.getPrincipal() == null) return;
        String username = session.getPrincipal().getName();

        System.out.println("채팅방 입장 : " + username);
    }

    /**
     * //메시지를 수신했을 때 실행된다.
     * @param session  채팅방 유저의 웹소켓 세션
     * @param message  채팅방번호와, 타입, 이름, 메시지가 담겨 있음, 바이트 크기도
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("=== handleTextMessage ===");
        String payload = message.getPayload();
        System.out.println("payload = " + payload);

        //입력 받은 JSON 문자열을 객체로 변환
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        //채팅방 번호로 채팅방 얻기
        ChatRoom chatRoom = repository.getChatRoom(chatMessage.getChatRoomId());
        //해당 채팅방에 메시지 전송
        chatRoom.handleMessage(session, chatMessage, objectMapper);
    }

    /**
     * close 이후 실행된다.
     * @param session 유저의 소켓 세션
     * @param status   닫힌 이유
     */
    @Override 
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("=== afterConnectionClosed ===");
        repository.removeUser(session, objectMapper);
    }
}