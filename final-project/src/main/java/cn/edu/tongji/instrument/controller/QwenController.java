package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.service.DoubaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class QwenController {

//    @Autowired
//    private QwenService qwenService;
//
//    @PostMapping
//    public String chat(@RequestParam String message) {
//        System.out.println("QwenController类里的chat方法调用了qwenService里的sendMessage方法。");
//        return qwenService.sendMessage(message);
//    }
    @Autowired
    private DoubaoService doubaoService;

    @PostMapping
    public String chat(@RequestParam String message) {
        System.out.println("QwenController类里的chat方法调用了doubaoService里的sendMessage方法。");
        return doubaoService.sendMessage(message);
    }
}
