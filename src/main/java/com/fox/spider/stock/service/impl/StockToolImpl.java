package com.fox.spider.stock.service.impl;

import com.fox.spider.stock.api.sina.SinaRealtimeDealInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockCategoryVo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.service.StockToolService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    /**
     * 获取涨跌幅限制
     *
     * @param stockVo
     * @param stockName
     * @return
     */
    @Override
    public BigDecimal limitRate(StockVo stockVo, String stockName) {
        if (null == stockVo) {
            return null;
        }

        Integer stockMarket = stockVo.getStockMarket();
        String stockCode = stockVo.getStockCode();

        //无涨跌幅限制的交易所
        if (StockConst.SM_NO_LIMIT_LIST.contains(stockMarket)) {
            return null;
        }

        //A股处理
        if (StockConst.SM_A_LIST.contains(stockMarket)) {
            if (null == stockCode || stockCode.isEmpty()) {
                return null;
            }
            StockCategoryVo stockCategoryVo = StockConst.stockCategory(stockCode, stockMarket);
            if (null == stockCategoryVo) {
                return null;
            }
            //科创板,创业版涨跌幅限制为20%
            if (StockConst.SK_SZ_STOCK_GEM == stockCategoryVo.getStockKind()
                    || StockConst.SK_SH_STOCK_STAR == stockCategoryVo.getStockKind()) {
                //科创板,创业版新股上市(前5日)无涨跌幅限制
                if (null != stockName && !stockName.isEmpty()) {
                    if (stockName.startsWith(StockConst.STOCK_NAME_NEW)
                            || stockName.startsWith(StockConst.STOCK_NAME_C)) {
                        return null;
                    }
                }

                return new BigDecimal(0.2).setScale(2, RoundingMode.HALF_UP);
            }
            //非ST的股票涨跌幅限制为10%，ST的股票涨跌幅限制为5%
            double limitRate = null != stockName && stockName.contains(StockConst.STOCK_NAME_ST) ? 0.05 : 0.1;
            return new BigDecimal(limitRate).setScale(2, RoundingMode.HALF_UP);
        }
        return null;
    }
}
