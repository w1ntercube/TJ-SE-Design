package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.service.QwenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class QwenController {

    @Autowired
    private QwenService qwenService;

    @PostMapping
    public String chat(@RequestParam String message) {
        return qwenService.sendMessage(message);
    }
}
