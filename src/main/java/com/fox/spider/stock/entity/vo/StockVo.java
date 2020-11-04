package com.fox.spider.stock.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 股票基本信息
 *
 * @author lusongsong
 * @date 2020/11/4 15:45
 */
@Data
@AllArgsConstructor
public class StockVo {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票所属交易锁
     */
    Integer stockMarket;
}
