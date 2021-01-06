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
     * 获取long类对象
     *
     * @param longStr
     * @return
     */
    public static Long initLong(String longStr) {
        return new BigDecimal(longStr).longValue();
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
     * 获取百分比BigDecimal对象
     *
     * @param rate
     * @return
     */
    public static BigDecimal initRate(String rate) {
        return new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
