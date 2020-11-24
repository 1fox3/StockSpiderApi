package com.fox.spider.stock.service;

import com.fox.spider.stock.entity.vo.StockVo;

import java.math.BigDecimal;

/**
 * 股票相关工具
 *
 * @author lusongsong
 * @date 2020/11/24 11:55
 */
public interface StockToolService {
    /**
     * 获取最新交易日
     *
     * @param stockMarket
     * @return
     */
    String lastDealDate(Integer stockMarket);

    /**
     * 获取涨跌幅限制
     *
     * @param stockVo
     * @param stockName
     * @return
     */
    BigDecimal limitRate(StockVo stockVo, String stockName);
}
