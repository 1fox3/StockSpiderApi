package com.fox.spider.stock.constant;

import com.fox.spider.stock.util.DateUtil;

import java.util.*;

/**
 * 股市交易状态常量
 *
 * @author lusongsong
 * @date 2020/11/9 16:50
 */
public class StockMarketStatusConst {
    /**
     * 未知的状态
     */
    public static final int UNKNOWN = 0;
    /**
     * 未知描述
     */
    public static final String DESC_UNKNOWN = "未知";
    /**
     * 开盘
     */
    public static final int OPEN = 1;
    /**
     * 开盘描述
     */
    public static final String DESC_OPEN = "开盘中";
    /**
     * 收盘
     */
    public static final int CLOSE = 2;
    /**
     * 收盘描述
     */
    public static final String DESC_CLOSE = "已收盘";
    /**
     * 休市
     */
    public static final int REST = 3;
    /**
     * 休市描述
     */
    public static final String DESC_REST = "休市";
    /**
     * 竞价
     */
    public static final int COMPETE = 4;
    /**
     * 竞价描述
     */
    public static final String DESC_COMPETE = "竞价中";
    /**
     * 未开盘
     */
    public static final int INIT = 5;
    /**
     * 未开盘描述
     */
    public static final String DESC_INIT = "未开盘";
    /**
     * 午间休息
     */
    public static final int NOON = 6;
    /**
     * 午间休息描述
     */
    public static final String DESC_NOON = "午间休息";
    /**
     * 即将开盘
     */
    public static final int SOON = 7;
    /**
     * 即将开盘藐视
     */
    public static final String DESC_SOON = "即将开盘";
    /**
     * 状态名称对应
     */
    public static final Map<Integer, String> STATUS_DESC_MAP = new HashMap<Integer, String>() {{
        put(UNKNOWN, DESC_UNKNOWN);
        put(OPEN, DESC_OPEN);
        put(CLOSE, DESC_CLOSE);
        put(REST, DESC_REST);
        put(COMPETE, DESC_COMPETE);
        put(INIT, DESC_INIT);
        put(NOON, DESC_NOON);
        put(SOON, DESC_SOON);
    }};

    /**
     * 可以交易的状态列表
     */
    public static final List<Integer> CAN_DEAL_STATUS_LIST = Arrays.asList(OPEN, COMPETE);

    /**
     * A股交易日不同时间交易状态
     *
     * @return
     */
    public static Integer aTimeSMStatus() {
        Calendar calendar = DateUtil.currentTime();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
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

    /**
     * 港股交易日不同时间交易状态
     *
     * @return
     */
    public static Integer hkTimeSMStatus() {
        Calendar calendar = DateUtil.currentTime();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
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

    /**
     * 获取股市交易日不同时间交易状态
     *
     * @param stockMarket
     * @return
     */
    public static Integer timeSMStatus(Integer stockMarket) {
        if (null == stockMarket) {
            return null;
        }
        switch (stockMarket) {
            case StockConst.SM_SH:
            case StockConst.SM_SZ:
                return aTimeSMStatus();
            case StockConst.SM_HK:
                return hkTimeSMStatus();
            default:
                return null;
        }
    }
}
