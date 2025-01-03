package cn.edu.tongji.instrument.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthService {

    private final Map<String, String> otpStorage = new HashMap<>();

    /**
     * 发送短信验证码
     *
     * @param phoneNumber 用户手机号
     * @return 发送的验证码
     */
    public String sendSMS(String phoneNumber) {
        String otp = generateOTP();
        otpStorage.put(phoneNumber, otp);
        // 没有实现真正的短信发送功能，这里模拟发送短信验证码
        System.out.println("向手机号 " + phoneNumber + " 发送验证码: " + otp);
        return otp;
    }

    /**
     * 验证用户输入的验证码
     *
     * @param phoneNumber 用户手机号
     * @param otp 用户输入的验证码
     * @return 验证结果
     */
    public boolean verifyOTP(String phoneNumber, String otp) {
        String storedOtp = otpStorage.get(phoneNumber);
        return storedOtp != null && storedOtp.equals(otp);
    }

    /**
     * 生成6位随机验证码
     *
     * @return 验证码
     */
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
