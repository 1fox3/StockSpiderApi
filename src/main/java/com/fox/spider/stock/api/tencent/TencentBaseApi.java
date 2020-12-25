package com.fox.spider.stock.api.tencent;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯股票数据接口基类
 *
 * @author lusongsong
 * @date 2020/12/24 11:29
 */
public class TencentBaseApi {
    /**
     * 网易支持的股市列表
     */
    public static final List<Integer> TENCENT_SUPPORT_SM = Arrays.asList(
            StockConst.SM_SH,
            StockConst.SM_SZ,
            StockConst.SM_HK
    );
    /**
     * 腾讯股市对应的拼音
     */
    public static final Map<Integer, String> TENCENT_SM_PY_MAP = new HashMap<Integer, String>() {{
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
    public static String tencentStockMarketPY(Integer stockMarket) {
        return TencentBaseApi.TENCENT_SM_PY_MAP.containsKey(stockMarket) ?
                TencentBaseApi.TENCENT_SM_PY_MAP.get(stockMarket) : "sh";
    }

    /**
     * 获取新浪股票代码
     *
     * @param stockVo
     * @return
     */
    public static String tencentStockCode(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockCode() || stockVo.getStockCode().isEmpty()
                || null == stockVo.getStockMarket() || !StockConst.SM_ALL.contains(stockVo.getStockMarket())) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(TencentBaseApi.tencentStockMarketPY(stockVo.getStockMarket()));
        stringBuffer.append(stockVo.getStockCode());
        return stringBuffer.toString();
    }

    /**
     * 获取股票信息
     *
     * @param tencnetStockCode
     * @return
     */
    public static StockVo tencnetStockCodeToStockVo(String tencnetStockCode) {
        if (null == tencnetStockCode || tencnetStockCode.isEmpty()) {
            return null;
        }
        for (Integer stockMarket : TencentBaseApi.TENCENT_SM_PY_MAP.keySet()) {
            String stockMarketPY = TencentBaseApi.TENCENT_SM_PY_MAP.get(stockMarket);
            if (tencnetStockCode.startsWith(stockMarketPY)) {
                String stockCode = tencnetStockCode.replace(stockMarketPY, "");
                return new StockVo(stockCode, stockMarket);
            }
        }
        return null;
    }

}