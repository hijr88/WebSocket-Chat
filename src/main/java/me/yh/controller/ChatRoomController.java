package me.yh.controller;

import me.yh.chatroom.model.ChatRoom;
import me.yh.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository repository;

    @Autowired
    public ChatRoomController(ChatRoomRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/rooms")
    public String rooms(Model model) {
        model.addAttribute("rooms", repository.getChatRooms());
        return "/chat/room-list";
    }

    @GetMapping("/rooms/{id}")
    public String room(@PathVariable Long id, Model model, Principal principal) {
        ChatRoom room = repository.getChatRoom(id);
        if(room == null) {
            return "redirect:/chat/rooms";
        }
        model.addAttribute("room", room);
        model.addAttribute("username", principal.getName()); // 회원 이름

        return "/chat/room";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/rooms/create", consumes = "text/plain")
    public ResponseEntity<Integer> createRoom(@RequestBody String name) {
        repository.add(name);
        return ResponseEntity.ok(1);
    }

    @GetMapping("")
    public String index() {
        return "index";
    }
}