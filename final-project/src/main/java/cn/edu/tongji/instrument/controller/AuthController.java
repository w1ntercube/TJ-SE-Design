package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.service.SMSService;
import cn.edu.tongji.instrument.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SMSService smsService;

    @Autowired
    private UserService userService;

    /**
     * 发送验证码到指定手机号
     *
     * @param phoneNumber 用户手机号
     * @return 返回信息
     */
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOTP(@RequestParam String phoneNumber) {
        String otp = smsService.sendSMS(phoneNumber);
        return ResponseEntity.ok("验证码已发送到 " + phoneNumber);
    }

    /**
     * 校验验证码并设置新密码
     *
     * @param phoneNumber 用户手机号
     * @param otp 用户输入的验证码
     * @param newPassword 用户输入的新密码
     * @return 返回信息
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String phoneNumber,
            @RequestParam String otp,
            @RequestParam String newPassword
    ) {
        // 验证验证码
        boolean isValidOtp = smsService.verifyOTP(phoneNumber, otp);

        if (!isValidOtp) {
            return ResponseEntity.badRequest().body("验证码错误或已过期");
        }

        // 更新用户密码
        userService.updatePassword(phoneNumber, newPassword);

        return ResponseEntity.ok("密码已重置成功");
    }
}
