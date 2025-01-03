package cn.edu.tongji.instrument.service;


import cn.edu.tongji.instrument.dto.Message;
import cn.edu.tongji.instrument.dto.RequestBody;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class ChatService {

    @Value("${dashscope.api.key}")
    private String apiKey;

    @Value("${dashscope.api.url}")
    private String apiUrl;

    private final Gson gson = new Gson();

    public String sendMessage(String userMessage) {
        try {
            // 构建请求体
            RequestBody requestBody = new RequestBody(
                    "qwen-plus",
                    new Message[] {
                            new Message("system", "你是一个乐器知识相关的专家，只能回答与乐器知识相关的问题。" +
                                    "回答内容以纯文本的形式显示，不要超过300字。" +
                                    "对于与乐器无关的问题，一律回答：抱歉，这不是乐器知识方面的问题哦！"),
                            new Message("user", userMessage)
                    }
            );

            // 将请求体转换为 JSON
            String jsonInputString = gson.toJson(requestBody);

            // 创建 URL 对象
            URL url = new URL(apiUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // 设置请求方法和请求头
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // 启用输入输出流
            httpURLConnection.setDoOutput(true);

            // 写入请求体
            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 获取响应码
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                // 获取错误响应
                try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    throw new RuntimeException("Failed: HTTP error code: " + responseCode + ", Error Response: " + errorResponse);
                }
            }

            // 读取成功响应
            try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while sending message: " + e.getMessage(), e);
        }
    }

}