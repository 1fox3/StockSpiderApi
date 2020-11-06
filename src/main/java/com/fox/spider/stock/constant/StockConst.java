package com.fox.spider.stock.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 股票相关静态常量
 *
 * @author lusongsong
 * @date 2020/11/4 15:37
 */
public class StockConst {
    /**
     * 未知
     */
    public static final Integer SM_UNKNOWN = 0;
    /**
     * A股(沪深)
     */
    public static final Integer SM_A = 1;
    /**
     * 沪市
     */
    public static final Integer SM_SH = 1;
    /**
     * 深市
     */
    public static final Integer SM_SZ = 2;
    /**
     * 港式
     */
    public static final Integer SM_HK = 3;
    /**
     * 全部列表
     */
    public static final List<Integer> SM_ALL = Arrays.asList(SM_SH, SM_SZ, SM_HK);
    /**
     * A股列表
     */
    public static final List<Integer> SM_A_LIST = Arrays.asList(SM_SH, SM_SZ);

    /**
     * 未知
     */
    public static final Integer ST_UNKNOWN = 0;
    /**
     * 指数
     */
    public static final Integer ST_INDEX = 1;
    /**
     * 股票
     */
    public static final Integer ST_STOCK = 2;

    /**
     * 科创版
     */
    public static final Integer SK_STAR = 4;
    /**
     * 创业版
     */
    public static final Integer SK_GEM = 10;

    /**
     * st股票名称标识
     */
    public static final String STOCK_NAME_ST = "ST";
    /**
     * 新上市股票名称标识
     */
    public static final String STOCK_NAME_NEW = "N";

    /**
     * 前复权
     */
    public static final Integer SFQ_BEFORE = 1;
    /**
     * 后复权
     */
    public static final Integer SFQ_AFTER = 2;

    /**
     * 日期类型（天）
     */
    public static final Integer DT_DAY = 1;
    /**
     * 日期类型（周）
     */
    public static final Integer DT_WEEK = 2;
    /**
     * 日期类型（月）
     */
    public static final Integer DT_MONTH = 3;
}
