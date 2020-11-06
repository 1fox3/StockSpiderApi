package com.fox.spider.stock.api.nets;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;

import java.util.HashMap;
import java.util.Map;

/**
 * 网易股票数据接口基类
 *
 * @author lusongsong
 * @date 2020/11/5 16:47
 */
public class NetsBaseApi {
    /**
     * 股市对应的拼音
     */
    public static Map<Integer, String> stockMarketPYMap = new HashMap<Integer, String>() {{
        put(StockConst.SM_SH, "hs");//沪
        put(StockConst.SM_SZ, "hs");//深
//        put(StockConst.SM_HK, "hk");//港
    }};

    /**
     * 股票id对应的前缀
     */
    public static Map<Integer, String> stockMarketPreCodeMap = new HashMap<Integer, String>() {{
        put(StockConst.SM_SH, "0");//沪
        put(StockConst.SM_SZ, "1");//深
//        put(StockConst.SM_HK, "");//港
    }};

    /**
     * 获取股票集市对应的拼音
     *
     * @param stockMarket
     * @return
     */
    public static String netsStockMarketPY(Integer stockMarket) {
        return NetsBaseApi.stockMarketPYMap.containsKey(stockMarket) ?
                NetsBaseApi.stockMarketPYMap.get(stockMarket) : "";
    }

    /**
     * 获取股票编码对应的前缀
     *
     * @param stockMarket
     * @return
     */
    protected static String netsStockPreCode(Integer stockMarket) {
        return NetsBaseApi.stockMarketPreCodeMap.containsKey(stockMarket) ?
                NetsBaseApi.stockMarketPreCodeMap.get(stockMarket) : "";
    }

    /**
     * 获取网易对应的股票编码
     *
     * @param stockVo
     * @return
     */
    public static String netsStockCode(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockCode() || stockVo.getStockCode().isEmpty()
                || null == stockVo.getStockMarket()) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(NetsBaseApi.netsStockPreCode(stockVo.getStockMarket()));
        stringBuffer.append(stockVo.getStockCode());
        return stringBuffer.toString();
    }
}
