package com.fox.spider.stock.entity.po.hk;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 港股K线数据点数据
 *
 * @author lusongsong
 * @date 2021/1/13 11:16
 */
@Data
public class HKKLineNodeDataPo implements Serializable {
    /**
     * 日期
     */
    String dt;
    /**
     * 时间
     */
    String time;
    /**
     * 收盘价
     */
    BigDecimal closePrice;
    /**
     * 开盘价
     */
    BigDecimal openPrice;
    /**
     * 最高价
     */
    BigDecimal highestPrice;
    /**
     * 最低价
     */
    BigDecimal lowestPrice;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
}
