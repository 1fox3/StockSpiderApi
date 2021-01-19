package com.fox.spider.stock.api.nets;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    public static final Map<Integer, String> NETS_SM_PY_MAP = new HashMap<Integer, String>() {{
        put(StockConst.SM_SH, "hs");//沪
        put(StockConst.SM_SZ, "hs");//深
    }};
    /**
     * 股票id对应的前缀
     */
    public static final Map<Integer, String> NETS_SM_PRE_CODE_MAP = new HashMap<Integer, String>() {{
        put(StockConst.SM_SH, "0");//沪
        put(StockConst.SM_SZ, "1");//深
    }};

    /**
     * 获取股票集市对应的拼音
     *
     * @param stockMarket
     * @return
     */
    public static String netsStockMarketPY(Integer stockMarket) {
        return NetsBaseApi.NETS_SM_PY_MAP.containsKey(stockMarket) ?
                NetsBaseApi.NETS_SM_PY_MAP.get(stockMarket) : "";
    }

    /**
     * 获取股票编码对应的前缀
     *
     * @param stockMarket
     * @return
     */
    protected static String netsStockPreCode(Integer stockMarket) {
        return NetsBaseApi.NETS_SM_PRE_CODE_MAP.containsKey(stockMarket) ?
                NetsBaseApi.NETS_SM_PRE_CODE_MAP.get(stockMarket) : "";
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

    /**
     * 获取股票信息
     *
     * @param netsStockCode
     * @return
     */
    public static StockVo netsStockCodeToStockVo(String netsStockCode) {
        if (null == netsStockCode || netsStockCode.isEmpty()) {
            return null;
        }
        for (Integer stockMarket : NETS_SM_PRE_CODE_MAP.keySet()) {
            String stockMarketPreCode = NETS_SM_PRE_CODE_MAP.get(stockMarket);
            if (netsStockCode.startsWith(stockMarketPreCode)) {
                String stockCode = netsStockCode.replaceFirst(stockMarketPreCode, "");
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
        return NETS_SM_PRE_CODE_MAP.containsKey(stockMarket);
    }
}
