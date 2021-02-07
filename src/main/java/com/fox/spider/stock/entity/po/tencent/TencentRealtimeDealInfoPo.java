package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.List;

/**
 * 腾讯股票实时成交信息
 *
 * @author lusongsong
 * @date 2020/12/24 14:17
 */
@Data
public class TencentRealtimeDealInfoPo implements Serializable {
    /**
     * 股票交易所
     */
    Integer stockMarket;
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 当前日期
     */
    String dt;
    /**
     * 当前时间
     */
    String time;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 股票英文名
     */
    String stockNameEn;
    /**
     * 当前价格
     */
    BigDecimal currentPrice;
    /**
     * 今日开盘价
     */
    BigDecimal openPrice;
    /**
     * 均价
     */
    BigDecimal avgPrice;
    /**
     * 今日最高价
     */
    BigDecimal highestPrice;
    /**
     * 今日最低价
     */
    BigDecimal lowestPrice;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 涨停价
     */
    BigDecimal upLimitPrice;
    /**
     * 跌停价
     */
    BigDecimal downLimitPrice;
    /**
     * 价格涨幅
     */
    BigDecimal uptickPrice;
    /**
     * 增长率
     */
    BigDecimal uptickRate;
    /**
     * 振幅
     */
    BigDecimal surgeRate;
    /**
     * 成交股数
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 盘后成交股数
     */
    Long closeDealNum;
    /**
     * 盘后成交金额
     */
    BigDecimal closeDealMoney;
    /**
     * 排名靠前的5个买方报价
     */
    TreeMap<BigDecimal, Long> buyPriceMap;
    /**
     * 排名靠前的5个卖方报价
     */
    TreeMap<BigDecimal, Long> sellPriceMap;
    /**
     * 总市值
     */
    BigDecimal totalValue;
    /**
     * 流通值
     */
    BigDecimal circValue;
    /**
     * 换手率
     */
    BigDecimal turnoverRate;
    /**
     * 市净率
     */
    BigDecimal pbrRate;
    /**
     * 市盈率(TTM)
     */
    BigDecimal perRate;
    /**
     * 市盈率(动)
     */
    BigDecimal perDynamicRate;
    /**
     * 市盈率(静)
     */
    BigDecimal perStaticRate;
    /**
     * 量比
     */
    BigDecimal qrrRate;
    /**
     * 委差
     */
    Long committeeSent;
    /**
     * 52周最高价
     */
    BigDecimal fiftyTwoWeekHighestPrice;
    /**
     * 51周最低阶
     */
    BigDecimal fiftyTwoWeekLowestPrice;
    /**
     * 未知的数据列表
     */
    List<String> unknownKeyList;
}
