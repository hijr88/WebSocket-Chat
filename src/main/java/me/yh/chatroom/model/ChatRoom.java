package me.yh.chatroom.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class ChatRoom implements Comparable<ChatRoom> {
    private static final AtomicLong seq = new AtomicLong(0L); //DB 시퀀스라고 가정
    private Long id;    //방번호
    private String name; //방이름
    private LocalDateTime create; //방 생성일자
    private LocalDateTime lastChatTime; //방 마지막 채팅시간
    private final Set<WebSocketSession> sessions = new HashSet<>(); //해당 방에 접속한 클라이언트한테 보낼수 있는 SockJsSession

    // 채팅방 생성
    public static ChatRoom create(@NonNull String name) {
        ChatRoom room = new ChatRoom();
        seq.set(seq.get()+1);    //시퀀스 1증가
        room.id = seq.get();     //아이디에 시퀀스 부여
        room.name = name;        //채팅방 이름
        room.create = LocalDateTime.now();  //생성 시간
        room.lastChatTime = room.create;
        return room;
    }

    /**
     * @param session       유저(웹소켓)세션
     * @param chatMessage   클라에서 보내온 메시지
     * @param objectMapper  JSON 변환하기 위한 객체
     */
    public void handleMessage(WebSocketSession session, ChatMessage chatMessage, ObjectMapper objectMapper) {
        System.out.println("=== handleMessage ===");
        System.out.println(chatMessage);
        this.lastChatTime = LocalDateTime.now();

        if (chatMessage.getType() == MessageType.JOIN) { // 메시지가 가입일 경우
            join(session);
            chatMessage.setMessage(chatMessage.getUsername() + "님이 입장했습니다.");
        } else if (chatMessage.getType() == MessageType.LEAVE) {
            chatMessage.setMessage(chatMessage.getUsername() + "님이 퇴장하셨습니다.");
        }
        try { //객체를 JSON 으로 변경하여 전송 호출
            final String json = objectMapper.writeValueAsString(chatMessage);
            send(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void join(WebSocketSession session) {
        sessions.add(session);
    }

    /**
     * @param json JSON으로 변환된 메시지
     *  해당 채팅방을 보고 있는 모든 세션한테 전송한다.
     */
    private void send(String json) {
        TextMessage message = new TextMessage(json);
        sessions.parallelStream().forEach(session -> {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                e.getStackTrace();
            }
        });
    }

    /**
     * @param target 채팅방에서 해당 웹소켓(유저) 세션을 제거
     */
    public void remove(WebSocketSession target, ObjectMapper objectMapper) {
        String targetId = target.getId();
        final boolean remove = sessions.removeIf(session -> session.getId().equals(targetId));
        if (remove) {
            ChatMessage message = new ChatMessage();
            message.setChatRoomId(this.getId());
            message.setType(MessageType.LEAVE);
            message.setUsername(target.getPrincipal().getName());
            handleMessage(null,message,objectMapper);
        }
    }

    @Override
    public int compareTo(ChatRoom o) { //가장 마지막에 채팅한 방을 위로
        if ( this.lastChatTime.isBefore(o.lastChatTime) ) return 1;
        else return -1;
    }
}