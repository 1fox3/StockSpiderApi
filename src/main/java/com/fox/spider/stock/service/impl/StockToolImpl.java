package com.fox.spider.stock.service.impl;

import com.fox.spider.stock.api.sina.SinaRealtimeDealInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.service.StockToolService;
import org.springframework.stereotype.Service;

/**
 * 股票工具服务实现
 *
 * @author lusongsong
 * @date 2020/11/24 13:40
 */
@Service
public class StockToolImpl implements StockToolService {
    /**
     * 获取最新交易日
     *
     * @param stockMarket
     * @return
     */
    @Override
    public String lastDealDate(Integer stockMarket) {
        SinaRealtimeDealInfoApi sinaRealtimeDealInfoApi = new SinaRealtimeDealInfoApi();
        if (StockConst.DEMO_STOCK.containsKey(stockMarket)) {
            StockVo stockVo = StockConst.DEMO_STOCK.get(stockMarket);
            if (null != stockVo) {
                SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = sinaRealtimeDealInfoApi.realtimeDealInfo(stockVo);
                if (null != sinaRealtimeDealInfoPo && null != sinaRealtimeDealInfoPo.getDt()) {
                    return sinaRealtimeDealInfoPo.getDt();
                }
            }
        }
        return "";
    }
}
