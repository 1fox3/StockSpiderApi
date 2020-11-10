package com.fox.spider.stock.util;

import com.fox.spider.stock.api.sina.SinaRealtimeDealInfo;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;

/**
 * 股票相关工具类
 *
 * @author lusongsong
 * @date 2020/11/9 15:27
 */
public class StockUtil {
    /**
     * 获取股市最新交易日
     *
     * @param stockMarket
     * @return
     */
    public static String lastDealDate(Integer stockMarket) {
        SinaRealtimeDealInfo sinaRealtimeDealInfo = new SinaRealtimeDealInfo();
        if (StockConst.DEMO_STOCK.containsKey(stockMarket)) {
            StockVo stockVo = StockConst.DEMO_STOCK.get(stockMarket);
            if (null != stockVo) {
                SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = sinaRealtimeDealInfo.realtimeDealInfo(stockVo);
                if (null != sinaRealtimeDealInfoPo && null != sinaRealtimeDealInfoPo.getDt()) {
                    return sinaRealtimeDealInfoPo.getDt();
                }
            }
        }
        return "";
    }
}
