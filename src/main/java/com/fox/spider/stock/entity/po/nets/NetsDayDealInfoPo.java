package com.fox.spider.stock.entity.po.nets;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 按天成交信息
 *
 * @author lusongsong
 * @date 2020/11/6 13:54
 */
@Data
public class NetsDayDealInfoPo {
    /**
     * 交易日期
     */
    String dt;
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 收盘价
     */
    BigDecimal closePrice;
    /**
     * 最高价
     */
    BigDecimal highestPrice;
    /**
     * 最低价
     */
    BigDecimal lowestPrice;
    /**
     * 开盘价
     */
    BigDecimal openPrice;
    /**
     * 前收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 涨跌额
     */
    BigDecimal uptickPrice;
    /**
     * 涨跌幅
     */
    BigDecimal uptickRate;
    /**
     * 换手率
     */
    BigDecimal turnoverRate;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 总市值
     */
    BigDecimal totalValue;
    /**
     * 流通值
     */
    BigDecimal circValue;
}
