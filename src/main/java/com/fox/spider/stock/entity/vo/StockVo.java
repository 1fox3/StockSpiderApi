package com.fox.spider.stock.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 股票基本信息
 *
 * @author lusongsong
 * @date 2020/11/4 15:45
 */
@Data
@AllArgsConstructor
public class StockVo implements Serializable {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票所属交易所
     */
    Integer stockMarket;

    /**
     * 验证对象是否正确
     *
     * @param stockVo
     * @return
     */
    public static final boolean verify(StockVo stockVo) {
        return null != stockVo && null != stockVo.getStockMarket() && null != stockVo.getStockCode();
    }
}
