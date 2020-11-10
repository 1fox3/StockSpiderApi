package com.fox.spider.stock.constant;

import com.fox.spider.stock.entity.vo.StockVo;

import java.util.*;

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
     * 未知名称
     */
    public static final String SM_NAME_UNKNOWN = "未知";
    /**
     * A股(沪深)
     */
    public static final Integer SM_A = 1;
    /**
     * A股名称
     */
    public static final String SM_NAME_A = "A股";
    /**
     * 沪市
     */
    public static final Integer SM_SH = 1;
    /**
     * 沪市名称
     */
    public static final String SM_NAME_SH = "沪市";
    /**
     * 深市
     */
    public static final Integer SM_SZ = 2;
    /**
     * 深市名称
     */
    public static final String SM_NAME_SZ = "深市";
    /**
     * 港股
     */
    public static final Integer SM_HK = 3;
    /**
     * 港股名称
     */
    public static final String SM_NAME_HK = "港股";
    /**
     * 全部列表
     */
    public static final List<Integer> SM_ALL = Arrays.asList(SM_SH, SM_SZ, SM_HK);
    /**
     * A股列表
     */
    public static final List<Integer> SM_A_LIST = Arrays.asList(SM_SH, SM_SZ);
    /**
     * 无涨跌幅限制的交易所
     */
    public static final List<Integer> SM_NO_LIMIT_LIST = Arrays.asList(SM_HK);
    /**
     * 股市名称对应
     */
    public static final Map<Integer, String> SM_NAME_MAP = new HashMap<Integer, String>() {{
        put(SM_UNKNOWN, SM_NAME_UNKNOWN);
        put(SM_A, SM_NAME_A);
        put(SM_SH, SM_NAME_SH);
        put(SM_SZ, SM_NAME_SZ);
        put(SM_HK, SM_NAME_HK);
    }};

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

    /**
     * A股重点指数
     */
    public static final List<StockVo> TOP_INDEX_A = Arrays.asList(
            //上证指数
            new StockVo("000001", SM_SH),
            //深证成指
            new StockVo("399001", SM_SZ),
            //创业板指
            new StockVo("399006", SM_SZ)
    );
    /**
     * 港股重点指数
     */
    public static final List<StockVo> TOP_INDEX_HK = Arrays.asList(
            //恒生指数
            new StockVo("HSI", SM_HK),
            //国企指数
            new StockVo("HSCEI", SM_HK)
    );

    /**
     * 股市参照股票
     */
    public static final Map<Integer, StockVo> DEMO_STOCK = new HashMap<Integer, StockVo>() {{
        put(SM_A, new StockVo("000001", SM_SH));
        put(SM_SH, new StockVo("000001", SM_SH));
        put(SM_SZ, new StockVo("399001", SM_SZ));
        put(SM_HK, new StockVo("HSI", SM_HK));
    }};

    /**
     * 获取股市名称
     *
     * @param stockMarket
     * @return
     */
    public static String stockMarketName(Integer stockMarket) {
        return SM_NAME_MAP.containsKey(stockMarket) ? SM_NAME_MAP.get(stockMarket) : "";
    }

    /**
     * 获取交易所top指标列表
     *
     * @param stockMarket
     * @return
     */
    public static List<StockVo> stockMarketTopIndex(Integer stockMarket) {
        if (SM_A_LIST.contains(stockMarket)) {
            return TOP_INDEX_A;
        }
        if (SM_HK.equals(stockMarket)) {
            return TOP_INDEX_HK;
        }
        return new ArrayList();
    }
}
