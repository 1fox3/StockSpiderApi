package com.fox.spider.stock.entity.po.sina;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 新浪网大额交易汇总信息
 *
 * @author lusongsong
 * @date 2021/1/11 14:20
 */
@Data
public class SinaBigDealSumPo implements Serializable {
    /**
     * 股票所属交易所
     */
    Integer stockMarket;
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 日期
     */
    String dt;
    /**
     * 大单平均成交价
     */
    BigDecimal avgPrice;
    /**
     * 成交股数
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 大单成交量
     */
    Long bigDealNum;
    /**
     * 大单成交金额
     */
    BigDecimal bigDealMoney;
    /**
     * 大单买入成交量
     */
    Long bigBuyDealNum;
    /**
     * 大单买入成交金额
     */
    BigDecimal bigBuyDealMoney;
    /**
     * 大单卖出成交量
     */
    Long bigSellDealNum;
    /**
     * 大单卖出成交金额
     */
    BigDecimal bigSellDealMoney;
    /**
     * 大单中性成交量
     */
    Long bigFlatDealNum;
    /**
     * 大单中性成交金额
     */
    BigDecimal bigFlatDealMoney;
    /**
     * 大单成交量占比
     */
    BigDecimal bigDealNumRatio;
    /**
     * 大单成交金额占比
     */
    BigDecimal bigDealMoneyRatio;
}
