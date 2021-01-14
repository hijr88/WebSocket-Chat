package me.yh.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.yh.chatroom.model.ChatRoom;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Repository
public class ChatRoomRepository {
    private final Map<Long, ChatRoom> chatRoomMap = new TreeMap<>();

    private final Collection<ChatRoom> chatRooms;

    /**
     * 처음엔 3개의 방으로 설정
     */
    public ChatRoomRepository() {
        /*chatRoomMap = Collections.unmodifiableMap(
                Stream.of(ChatRoom.create("1번방"), ChatRoom.create("2번방"), ChatRoom.create("3번방"))
                        .collect(Collectors.toMap(ChatRoom::getId, Function.identity())));*/
        String[] a = {"딸기","바나나","멜론"};

        for(int i=0; i<3; i++) {
            ChatRoom room = ChatRoom.create(a[i]);
            chatRoomMap.put(room.getId(),room);
        }

        chatRooms = Collections.unmodifiableCollection(chatRoomMap.values());
    }

    /**
     * @param id 해당 번호로 채팅방 리턴
     */
    public ChatRoom getChatRoom(Long id) {
        return chatRoomMap.get(id);
    }

    /**
     * 채팅방 목록을 넘길때 정렬해서 리턴
     */
    public List<ChatRoom> getChatRooms() {
        List<ChatRoom> list = new ArrayList<>(chatRoomMap.values());
        list.sort(ChatRoom::compareTo);//가장 마지막에 채팅한 방을 위로
        return list;
    }

    /**
     * @param name 해당 이름으로 채팅방 생성
     */
    public void add(String name) {
        ChatRoom room = ChatRoom.create(name);
        chatRoomMap.put(room.getId(),room);
    }

    /**
     * @param id 해당 방번호 삭제
     */
    public void remove(Long id) {
        this.chatRoomMap.remove(id);
    }

    /**
     * @param session       유저 세션
     * @param objectMapper  JSON 변환 객체
     */
    public void removeUser(WebSocketSession session, ObjectMapper objectMapper) {
        this.chatRooms.parallelStream().forEach(chatRoom -> chatRoom.remove(session, objectMapper));
    }
}