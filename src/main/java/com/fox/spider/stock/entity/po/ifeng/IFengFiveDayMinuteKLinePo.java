package com.fox.spider.stock.entity.po.ifeng;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 凤凰网5日分钟K线交易数据
 *
 * @author lusongsong
 * @date 2021/1/6 16:50
 */
@Data
public class IFengFiveDayMinuteKLinePo implements Serializable {
    /**
     * 股票所属交易所
     */
    Integer stockMarket;
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 日期
     */
    String dt;
    /**
     * 开盘价
     */
    BigDecimal openPrice;
    /**
     * 收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 最高价
     */
    BigDecimal highestPrice;
    /**
     * 最低价
     */
    BigDecimal lowestPrice;
    /**
     * 分钟数据
     */
    List<IFengFiveDayMinuteNodeDataPo> klineData;
}
