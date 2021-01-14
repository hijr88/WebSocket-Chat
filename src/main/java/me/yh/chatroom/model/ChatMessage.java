package me.yh.chatroom.model;

import lombok.*;

@Data
public class ChatMessage {
    private Long chatRoomId;
    private String username;
    private String message;
    private MessageType type;
}