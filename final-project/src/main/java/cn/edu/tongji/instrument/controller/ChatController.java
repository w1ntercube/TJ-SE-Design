package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public String chat(@RequestParam String message) {
        return chatService.sendMessage(message);
    }
}
