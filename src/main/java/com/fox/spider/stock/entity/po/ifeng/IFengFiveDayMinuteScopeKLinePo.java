package com.fox.spider.stock.entity.po.ifeng;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 凤凰网5日分钟K线交易数据
 *
 * @author lusongsong
 * @date 2021/1/6 16:50
 */
@Data
public class IFengFiveDayMinuteScopeKLinePo implements Serializable {
    /**
     * 日期
     */
    String dt;
    /**
     * 分钟小时时间
     */
    String time;
    /**
     * 开盘价
     */
    BigDecimal openPrice;
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
     * 成交金额
     */
    BigDecimal dealMoney;
}
