package com.fox.spider.stock.constant;

import com.fox.spider.stock.entity.vo.StockCategoryVo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.DateUtil;

import java.util.*;

/**
 * 股票相关静态常量
 *
 * @author lusongsong
 * @date 2020/11/4 15:37
 */
public class StockConst {
    //证券交易所
    /**
     * 未知
     */
    public static final int SM_UNKNOWN = 0;
    /**
     * 未知名称
     */
    public static final String SM_NAME_UNKNOWN = "未知";
    /**
     * A股(沪深)
     */
    public static final int SM_A = 1;
    /**
     * A股名称
     */
    public static final String SM_NAME_A = "A股";
    /**
     * 沪市
     */
    public static final int SM_SH = 1;
    /**
     * 沪市名称
     */
    public static final String SM_NAME_SH = "沪市";
    /**
     * 深市
     */
    public static final int SM_SZ = 2;
    /**
     * 深市名称
     */
    public static final String SM_NAME_SZ = "深市";
    /**
     * 港股
     */
    public static final int SM_HK = 3;
    /**
     * 港股名称
     */
    public static final String SM_NAME_HK = "港股";
    /**
     * 全部列表
     */
    public static final List<Integer> SM_ALL = Arrays.asList(SM_SH, SM_SZ, SM_HK);
    /**
     * 按股票编码划分的全部股市
     */
    public static final List<Integer> SM_CODE_ALL = Arrays.asList(StockConst.SM_A, StockConst.SM_HK);
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

    //股票类型
    /**
     * 未知
     */
    public static final int ST_UNKNOWN = 0;
    /**
     * 未知
     */
    public static final String ST_NAME_UNKNOWN = "未知";
    /**
     * 指数
     */
    public static final int ST_INDEX = 1;
    /**
     * 指数
     */
    public static final String ST_NAME_INDEX = "指数";
    /**
     * 股票
     */
    public static final int ST_STOCK = 2;
    /**
     * 股票
     */
    public static final String ST_NAME_STOCK = "股票";
    /**
     * 基金
     */
    public static final int ST_FUND = 3;
    /**
     * 基金
     */
    public static final String ST_NAME_FUND = "基金";
    /**
     * 债券
     */
    public static final int ST_BOND = 4;
    /**
     * 债券
     */
    public static final String ST_NAME_BOND = "债券";
    /**
     * 权证
     */
    public static final int ST_WARRANT = 5;
    /**
     * 权证
     */
    public static final String ST_NAME_WARRANT = "权证";

    //股票分类
    /**
     * 未知
     */
    public static final int SK_UNKNOWN = 0;
    /**
     * 沪指
     */
    public static final int SK_SH_INDEX = 1;
    /**
     * 沪A
     */
    public static final int SK_SH_STOCK_A = 2;
    /**
     * 沪B
     */
    public static final int SK_SH_STOCK_B = 3;
    /**
     * 科创板
     */
    public static final int SK_SH_STOCK_STAR = 4;
    /**
     * 沪基
     */
    public static final int SK_SH_FUND = 5;
    /**
     * 沪债
     */
    public static final int SK_SH_BOND = 6;
    /**
     * 深指
     */
    public static final int SK_SZ_INDEX = 7;
    /**
     * 深A
     */
    public static final int SK_SZ_STOCK_A = 8;
    /**
     * 深B
     */
    public static final int SK_SZ_STOCK_B = 9;
    /**
     * 创业板
     */
    public static final int SK_SZ_STOCK_GEM = 10;
    /**
     * 深基
     */
    public static final int SK_SZ_FUND = 11;
    /**
     * 深债
     */
    public static final int SK_SZ_BOND = 12;
    /**
     * 港指
     */
    public static final int SK_HK_INDEX = 13;
    /**
     * 港股
     */
    public static final int SK_HK_STOCK = 14;
    /**
     * 港基
     */
    public static final int SK_HK_FUND = 15;
    /**
     * 权证
     */
    public static final int SK_HK_WARRANT = 16;
    /**
     * 股票代码分类
     */
    public static final Map<Integer, Map<Integer, Map<Integer, List<String>>>> STOCK_CATEGORY_VERIFY_MAP =
            new HashMap<Integer, Map<Integer, Map<Integer, List<String>>>>() {{
                //沪市
                put(SM_SH, new HashMap<Integer, Map<Integer, List<String>>>() {{
                    //指数
                    put(ST_INDEX, new HashMap<Integer, List<String>>() {{
                        //沪指
                        put(
                                SK_SH_INDEX,
                                Arrays.asList("000")
                        );
                    }});
                    //股票
                    put(ST_STOCK, new HashMap<Integer, List<String>>() {{
                        //沪A
                        put(
                                SK_SH_STOCK_A,
                                Arrays.asList("600", "601", "603", "605")
                        );
                        //沪B
                        put(
                                SK_SH_STOCK_B,
                                Arrays.asList("900")
                        );
                        //科创
                        put(
                                SK_SH_STOCK_STAR,
                                Arrays.asList("688", "689")
                        );
                    }});
                    //基金
                    put(ST_FUND, new HashMap<Integer, List<String>>() {{
                        //沪基
                        put(
                                SK_SH_FUND,
                                Arrays.asList("500", "501", "502", "505", "510",
                                        "511", "512", "513", "515", "518", "519",
                                        "522", "523")
                        );
                    }});
                    //债券
                    put(ST_BOND, new HashMap<Integer, List<String>>() {{
                        //沪债
                        put(
                                SK_SH_BOND,
                                Arrays.asList("010", "018", "019", "020", "090",
                                        "091", "100", "102", "103", "104", "105",
                                        "106", "107", "108", "110", "113", "120",
                                        "122", "124", "127", "130", "132", "133",
                                        "134", "136", "140", "141", "143", "144",
                                        "147", "152", "155", "157", "160", "163",
                                        "171", "173", "175", "182", "190", "191",
                                        "192", "201", "202", "204", "751")
                        );
                    }});
                }});
                //深市
                put(SM_SZ, new HashMap<Integer, Map<Integer, List<String>>>() {{
                    //指数
                    put(ST_INDEX, new HashMap<Integer, List<String>>() {{
                        //深指
                        put(
                                SK_SZ_INDEX,
                                Arrays.asList("399")
                        );
                    }});
                    //股票
                    put(ST_STOCK, new HashMap<Integer, List<String>>() {{
                        //深A
                        put(
                                SK_SZ_STOCK_A,
                                Arrays.asList("000", "001", "002", "003")
                        );
                        //深B
                        put(
                                SK_SZ_STOCK_B,
                                Arrays.asList("200", "201")
                        );
                        //创业
                        put(
                                SK_SZ_STOCK_GEM,
                                Arrays.asList("300")
                        );
                    }});
                    //基金
                    put(ST_FUND, new HashMap<Integer, List<String>>() {{
                        //深基
                        put(
                                SK_SZ_FUND,
                                Arrays.asList("150", "159", "16", "184")
                        );
                    }});
                    //债券
                    put(ST_BOND, new HashMap<Integer, List<String>>() {{
                        //深债
                        put(
                                SK_SZ_BOND,
                                Arrays.asList("10", "111", "112", "120", "123",
                                        "127", "128", "131", "149", "190", "191")
                        );
                    }});
                }});
                //港股
                put(SM_HK, new HashMap<Integer, Map<Integer, List<String>>>() {{
                    //指数
                    put(ST_INDEX, new HashMap<Integer, List<String>>() {{
                        //港指
                        put(
                                SK_HK_INDEX,
                                Arrays.asList("HS")
                        );
                    }});
                    //股票
                    put(ST_STOCK, new HashMap<Integer, List<String>>() {{
                        //港股
                        put(
                                SK_HK_STOCK,
                                Arrays.asList("00", "01", "020", "021", "022",
                                        "023", "024", "025", "026", "027", "0285",
                                        "0286", "0287", "0288", "0289", "029", "033",
                                        "036", "037", "038", "039", "043", "046",
                                        "060", "061", "062", "068", "069", "080",
                                        "081", "082", "083", "084", "085", "086",
                                        "099")
                        );
                    }});
                    //基金
                    put(ST_FUND, new HashMap<Integer, List<String>>() {{
                        //港基
                        put(
                                SK_HK_FUND,
                                Arrays.asList("0280", "0281", "0282", "0283", "0284",
                                        "030", "031", "072", "073", "075", "090",
                                        "091", "098", "807", "828", "830", "831",
                                        "870")
                        );
                    }});
                    //权证
                    put(ST_WARRANT, new HashMap<Integer, List<String>>() {{
                        //权证
                        put(
                                SK_HK_WARRANT,
                                Arrays.asList("100", "101", "11", "121", "122",
                                        "123", "124", "125", "126", "127", "128",
                                        "129", "13", "14", "15", "16", "17",
                                        "18", "19", "2", "47", "480", "481",
                                        "5", "6")
                        );
                    }});
                }});
            }};

    /**
     * st股票名称标识
     */
    public static final String STOCK_NAME_ST = "ST";
    /**
     * 新上市股票名称标识
     */
    public static final String STOCK_NAME_NEW = "N";
    /**
     * 创业板不设涨跌幅限制的标识(除首日外)
     */
    public static final String STOCK_NAME_C = "C";

    /**
     * 不复权
     */
    public static final int SFQ_NO = 0;
    /**
     * 前复权
     */
    public static final int SFQ_BEFORE = 1;
    /**
     * 后复权
     */
    public static final int SFQ_AFTER = 2;

    /**
     * 日期类型（天）
     */
    public static final int DT_DAY = 1;
    /**
     * 日期类型（周）
     */
    public static final int DT_WEEK = 2;
    /**
     * 日期类型（月）
     */
    public static final int DT_MONTH = 3;

    /**
     * 涨跌类型（平）
     */
    public static final int UPTICK_TYPE_FLAT = 1;
    /**
     * 涨跌类型（涨）
     */
    public static final int UPTICK_TYPE_UP = 2;
    /**
     * 涨跌类型（涨停）
     */
    public static final int UPTICK_TYPE_LIMIT_UP = 3;
    /**
     * 涨跌类型（跌）
     */
    public static final int UPTICK_TYPE_DOWN = 4;
    /**
     * 涨跌类型（跌停）
     */
    public static final int UPTICK_TYPE_LIMIT_DOWN = 5;

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
        //上证指数
        put(SM_SH, new StockVo("000001", SM_SH));
        //深证成指
        put(SM_SZ, new StockVo("399001", SM_SZ));
        //恒生指数
        put(SM_HK, new StockVo("HSI", SM_HK));
    }};

    /**
     * 股市开始日期
     */
    public static final Map<Integer, String> SM_START_DATE = new HashMap<Integer, String>() {{
        //沪市
        put(SM_SH, "1990-12-19");
        //深市
        put(SM_SZ, "1991-04-03");
        //香港
        put(SM_HK, "2006-09-11");
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
        switch (stockMarket) {
            case SM_SH:
            case SM_SZ:
                return TOP_INDEX_A;
            case SM_HK:
                return TOP_INDEX_HK;
            default:
                return null;
        }
    }

    /**
     * 获取股票类别
     *
     * @param stockCode
     * @param stockMarket
     * @return
     */
    public static StockCategoryVo stockCategory(String stockCode, Integer stockMarket) {
        if (null == stockCode || stockCode.isEmpty() || null == stockMarket
                || !STOCK_CATEGORY_VERIFY_MAP.containsKey(stockMarket)) {
            return new StockCategoryVo();
        }
        Map<Integer, Map<Integer, List<String>>> stockTypeMap = STOCK_CATEGORY_VERIFY_MAP.get(stockMarket);
        for (Integer stockType : stockTypeMap.keySet()) {
            Map<Integer, List<String>> stockKindMap = stockTypeMap.get(stockType);
            for (Integer stockKind : stockKindMap.keySet()) {
                List<String> preCodeList = stockKindMap.get(stockKind);
                for (String preCode : preCodeList) {
                    if (stockCode.startsWith(preCode)) {
                        return new StockCategoryVo(stockMarket, stockType, stockKind);
                    }
                }
            }
        }
        return new StockCategoryVo();
    }

    /**
     * 股市开始日期
     *
     * @param stockMarket
     * @return
     */
    public static String stockMarketStartDate(Integer stockMarket) {
        return SM_START_DATE.containsKey(stockMarket) ? SM_START_DATE.get(stockMarket) : null;
    }

    /**
     * 股市开始年份
     *
     * @param stockMarket
     * @return
     */
    public static String stockMarketStartYear(Integer stockMarket) {
        if (SM_START_DATE.containsKey(stockMarket)) {
            String startDate = SM_START_DATE.get(stockMarket);
            return DateUtil.dateStrFormatChange(startDate, DateUtil.DATE_FORMAT_1, DateUtil.YEAR_FORMAT_1);
        }
        return null;
    }
}
