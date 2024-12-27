package cn.edu.tongji.instrument.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径跨域
                .allowedOrigins("http://localhost:8081") // 前端的地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true); // 允许携带 Cookie
    }

    // 配置静态资源路径映射，用于图片在前端显示
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 假设你的文件存储路径是项目根目录下的 uploads/images
        registry.addResourceHandler("/uploads/images/**")  // 对应访问路径
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/images/");  // 映射到项目根目录的 uploads/images

        // 映射头像图片路径
        registry.addResourceHandler("/uploads/avatars/**")  // 对应访问路径
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/avatars/");  // 映射到项目根目录的 uploads/avatars
    }
}
