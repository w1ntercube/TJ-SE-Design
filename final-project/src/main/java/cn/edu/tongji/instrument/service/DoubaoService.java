package cn.edu.tongji.instrument.service;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class DoubaoService {

    @Value("${doubao.api.key}")
    private String apiKey;

    @Value("${doubao.api.url}")
    private String apiUrl;

    @Value("${doubao.model.id}")
    private String modelId;

    private final Gson gson = new Gson();

    public String sendMessage(String userMessage) {
        try {
            // 构造请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", modelId);

            JsonArray messages = new JsonArray();

            JsonObject systemMsg = new JsonObject();
            systemMsg.addProperty("role", "system");
            systemMsg.addProperty("content", "你是一个乐器知识专家，你的名字是强尼，只能回答与乐器有关的问题，回答不要超过300字。对于无关问题统一回复：抱歉，这不是乐器知识方面的问题哦！");
            messages.add(systemMsg);

            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");
            userMsg.addProperty("content", userMessage);
            messages.add(userMsg);

            requestBody.add("messages", messages);
            requestBody.addProperty("stream", false);

            // 发起 HTTP 请求
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();

            StringBuilder responseBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }

            JsonObject responseJson = JsonParser.parseString(responseBuilder.toString()).getAsJsonObject();
            JsonArray choices = responseJson.getAsJsonArray("choices");

            if (choices != null && choices.size() > 0) {
                JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
                return message.get("content").getAsString();
            } else {
                return "⚠️ 未收到模型的有效回复";
            }

        } catch (Exception e) {
            return "❌ 请求失败：" + e.getMessage();
        }
    }
}
