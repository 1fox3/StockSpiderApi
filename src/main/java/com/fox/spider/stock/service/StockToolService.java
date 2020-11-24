package com.fox.spider.stock.service;

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
}
