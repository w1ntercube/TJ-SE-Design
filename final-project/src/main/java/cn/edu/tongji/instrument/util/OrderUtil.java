package cn.edu.tongji.instrument.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class OrderUtil {

    // 计算租期天数
    public static int calculateRentalDuration(LocalDateTime start, LocalDateTime end) {
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    // 计算押金比率（押金为总价的120%）
    public static BigDecimal calculateDeposit(BigDecimal totalPrice, double depositRate) {
        return totalPrice.multiply(BigDecimal.valueOf(depositRate));
    }
}
