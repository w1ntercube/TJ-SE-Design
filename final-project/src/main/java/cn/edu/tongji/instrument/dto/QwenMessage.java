package cn.edu.tongji.instrument.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QwenMessage {
    private String role;
    private String content;

    public QwenMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

}
// test