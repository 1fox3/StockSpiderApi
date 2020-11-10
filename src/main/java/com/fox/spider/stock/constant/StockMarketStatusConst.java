package com.fox.spider.stock.constant;

import java.util.HashMap;
import java.util.Map;

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
    public static final Integer UNKNOWN = 0;
    /**
     * 未知描述
     */
    public static final String DESC_UNKNOWN = "未知";
    /**
     * 开盘
     */
    public static final Integer OPEN = 1;
    /**
     * 开盘描述
     */
    public static final String DESC_OPEN = "开盘中";
    /**
     * 收盘
     */
    public static final Integer CLOSE = 2;
    /**
     * 收盘描述
     */
    public static final String DESC_CLOSE = "已收盘";
    /**
     * 休市
     */
    public static final Integer REST = 3;
    /**
     * 休市描述
     */
    public static final String DESC_REST = "休市";
    /**
     * 竞价
     */
    public static final Integer COMPETE = 4;
    /**
     * 竞价描述
     */
    public static final String DESC_COMPETE = "竞价中";
    /**
     * 未开盘
     */
    public static final Integer INIT = 5;
    /**
     * 未开盘描述
     */
    public static final String DESC_INIT = "未开盘";
    /**
     * 午间休息
     */
    public static final Integer NOON = 6;
    /**
     * 午间休息描述
     */
    public static final String DESC_NOON = "午间休息";
    /**
     * 即将开盘
     */
    public static final Integer SOON = 7;
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
}
