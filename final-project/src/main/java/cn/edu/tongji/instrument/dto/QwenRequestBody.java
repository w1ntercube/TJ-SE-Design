package cn.edu.tongji.instrument.dto;


public class QwenRequestBody {
    private String model;
    private QwenMessage[] qwenMessages;

    public QwenRequestBody(String model, QwenMessage[] qwenMessages) {
        this.model = model;
        this.qwenMessages = qwenMessages;
    }

    public QwenMessage[] getMessages() {
        return qwenMessages;
    }

    public void setMessages(QwenMessage[] qwenMessages) {
        this.qwenMessages = qwenMessages;
    }
}