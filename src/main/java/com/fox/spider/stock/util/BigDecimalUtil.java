package com.fox.spider.stock.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal工具类
 *
 * @author lusongsong
 * @date 2020/11/4 17:05
 */
public class BigDecimalUtil {
    /**
     * long值格式处理
     */
    public static final BigDecimal LONG_MULTIPLY_100 = new BigDecimal(100);
    /**
     * 价格格式处理
     */
    public static final BigDecimal PRICE_MULTIPLY_10000 = new BigDecimal(10000);
    /**
     * 百分比格式
     */
    public static final BigDecimal RATE_MULTIPLY_100 = new BigDecimal(100);

    /**
     * 获取long类对象
     *
     * @param longStr
     * @return
     */
    public static Long initLong(String longStr) {
        return new BigDecimal(longStr).longValue();
    }

    /**
     * 获取long类对象
     *
     * @param longStr
     * @param format
     * @return
     */
    public static Long initLong(String longStr, BigDecimal format) {
        return new BigDecimal(longStr).multiply(format).longValue();
    }

    /**
     * 获取价格BigDecimal类对象
     *
     * @param price
     * @return
     */
    public static BigDecimal initPrice(String price) {
        return new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取价格BigDecimal类对象
     *
     * @param price
     * @param format
     * @return
     */
    public static BigDecimal initPrice(String price, BigDecimal format) {
        return new BigDecimal(price).multiply(format).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取百分比BigDecimal对象
     *
     * @param rate
     * @return
     */
    public static BigDecimal initRate(String rate) {
        return new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取百分比BigDecimal对象
     *
     * @param rate
     * @param format
     * @return
     */
    public static BigDecimal initRate(String rate, BigDecimal format) {
        return new BigDecimal(rate).multiply(format).setScale(2, RoundingMode.HALF_UP);
    }
}
