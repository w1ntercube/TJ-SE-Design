package cn.edu.tongji.instrument.util;


import java.security.MessageDigest;
public class Md5Util {

    private static final String[] strDigits = {
            "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b",
            "c", "d", "e", "f", "g", "h",
            "j", "?", "~"
    };

    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16; // 高位
        int iD2 = iRet % 16; // 低位
        return strDigits[iD1] + strDigits[iD2];
    }

    private static String byteToString(byte[] bByte) {
        StringBuilder sBuffer = new StringBuilder();
        for (byte b : bByte) {
            sBuffer.append(byteToArrayString(b));
        }
        return sBuffer.toString();
    }

    /**
     * 计算字符串的 MD5 哈希值
     *
     * @param strObj 输入字符串
     * @return MD5 哈希值
     */
    public static String getMD5Code(String strObj) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(strObj.getBytes("UTF-8"));
            resultString = byteToString(digest); // 调用官方风格的字节转字符串方法
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }
}
