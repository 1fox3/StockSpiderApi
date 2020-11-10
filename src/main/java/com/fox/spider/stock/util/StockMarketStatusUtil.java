package com.fox.spider.stock.util;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.constant.StockMarketStatusConst;

import java.util.Calendar;
import java.util.Date;

/**
 * 股市交易状态工具类
 *
 * @author lusongsong
 * @date 2020/11/9 17:19
 */
public class StockMarketStatusUtil {
    /**
     * A股当前交易状态
     *
     * @return
     */
    public static Integer aCurrentSMStatus() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        //交易日状态
        if (DateUtil.getCurrentDate().equals(StockUtil.lastDealDate(StockConst.SM_A))) {
            if (0 <= hour && hour < 9) {
                return StockMarketStatusConst.INIT;
            } else if (9 <= hour && hour < 10) {
                if (0 <= minutes && minutes < 15) {
                    return StockMarketStatusConst.INIT;
                } else if (15 <= minutes && minutes < 25) {
                    return StockMarketStatusConst.COMPETE;
                } else if (25 <= minutes && minutes < 30) {
                    return StockMarketStatusConst.SOON;
                } else {
                    return StockMarketStatusConst.OPEN;
                }
            } else if (10 <= hour && hour < 11) {
                return StockMarketStatusConst.OPEN;
            } else if (11 <= hour && hour < 12) {
                if (30 > minutes) {
                    return StockMarketStatusConst.OPEN;
                } else {
                    return StockMarketStatusConst.NOON;
                }
            } else if (12 <= hour && hour < 13) {
                return StockMarketStatusConst.NOON;
            } else if (13 <= hour && hour < 15) {
                return StockMarketStatusConst.OPEN;
            } else {
                return StockMarketStatusConst.CLOSE;
            }
        }
        return StockMarketStatusConst.REST;
    }

    /**
     * 获取港股当前交易状态
     *
     * @return
     */
    public static Integer hkCurrentSMStatus() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        //交易日状态
        if (DateUtil.getCurrentDate().equals(StockUtil.lastDealDate(StockConst.SM_HK))) {
            if (0 <= hour && hour < 9) {
                return StockMarketStatusConst.INIT;
            } else if (9 <= hour && hour < 10) {
                if (0 <= minutes && minutes < 30) {
                    return StockMarketStatusConst.COMPETE;
                } else {
                    return StockMarketStatusConst.OPEN;
                }
            } else if (10 <= hour && hour < 12) {
                return StockMarketStatusConst.OPEN;
            } else if (12 <= hour && hour < 13) {
                return StockMarketStatusConst.NOON;
            } else if (13 <= hour && hour < 16) {
                return StockMarketStatusConst.OPEN;
            } else if (16 <= hour && hour < 17) {
                if (0 <= minutes && minutes < 10) {
                    return StockMarketStatusConst.COMPETE;
                } else {
                    return StockMarketStatusConst.CLOSE;
                }
            } else {
                return StockMarketStatusConst.CLOSE;
            }
        }
        return StockMarketStatusConst.REST;
    }

    /**
     * 获取股市当前交易状态
     *
     * @param stockMarket
     * @return
     */
    public static Integer currentSMStatus(Integer stockMarket) {
        if (StockConst.SM_A_LIST.contains(stockMarket)) {
            return aCurrentSMStatus();
        }
        if (StockConst.SM_HK.equals(stockMarket)) {
            return hkCurrentSMStatus();
        }
        return StockMarketStatusConst.UNKNOWN;
    }
}
