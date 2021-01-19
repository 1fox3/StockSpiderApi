package com.fox.spider.stock.api.ifeng;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;

import java.util.HashMap;
import java.util.Map;

/**
 * 凤凰网股票数据接口基类
 *
 * @author lusongsong
 * @date 2021/1/4 15:06
 */
public class IFengBaseApi {
    /**
     * 返回数据key
     */
    public static final String RESPONSE_KEY_RECORD = "record";
    /**
     * 凤凰网股市对应的拼音
     */
    public static final Map<Integer, String> IFENG_SM_PY_MAP = new HashMap<Integer, String>() {{
        put(StockConst.SM_SH, "sh");//沪
        put(StockConst.SM_SZ, "sz");//深
        put(StockConst.SM_HK, "hk");//港
    }};

    /**
     * 获取股市对应的拼音
     *
     * @param stockMarket
     * @return
     */
    public static String iFengStockMarketPY(Integer stockMarket) {
        return IFENG_SM_PY_MAP.containsKey(stockMarket) ?
                IFENG_SM_PY_MAP.get(stockMarket) : "sh";
    }

    /**
     * 获取凤凰网股票代码
     *
     * @param stockVo
     * @return
     */
    public static String iFengStockCode(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockCode() || stockVo.getStockCode().isEmpty()
                || null == stockVo.getStockMarket() || !StockConst.SM_ALL.contains(stockVo.getStockMarket())) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(iFengStockMarketPY(stockVo.getStockMarket()));
        stringBuffer.append(stockVo.getStockCode());
        return stringBuffer.toString();
    }

    /**
     * 获取股票信息
     *
     * @param iFengStockCode
     * @return
     */
    public static StockVo iFengStockCodeToStockVo(String iFengStockCode) {
        if (null == iFengStockCode || iFengStockCode.isEmpty()) {
            return null;
        }
        for (Integer stockMarket : IFENG_SM_PY_MAP.keySet()) {
            String stockMarketPY = IFENG_SM_PY_MAP.get(stockMarket);
            if (iFengStockCode.startsWith(stockMarketPY)) {
                String stockCode = iFengStockCode.replace(stockMarketPY, "");
                return new StockVo(stockCode, stockMarket);
            }
        }
        return null;
    }

    /**
     * 是否支持该证券所
     *
     * @param stockMarket
     * @return
     */
    public static boolean isSupport(int stockMarket) {
        return IFENG_SM_PY_MAP.containsKey(stockMarket);
    }
}
