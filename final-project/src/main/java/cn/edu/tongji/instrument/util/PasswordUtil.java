/*
package cn.edu.tongji.instrument.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    */
/**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     *//*

    public static String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    */
/**
     * 校验密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     *//*

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
*/
