package cn.edu.tongji.instrument.util;


import java.security.MessageDigest;

public class Md5Util {

    /**
     * 计算字符串的 MD5 哈希值
     *
     * @param data 输入字符串
     * @return MD5 哈希值
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
