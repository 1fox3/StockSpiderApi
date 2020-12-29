package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 腾信k线图点交易信息
 *
 * @author lusongsong
 * @date 2020/12/29 14:21
 */
@Data
public class TencentKLineNodeDataPo implements Serializable {
    /**
     * 日期
     */
    String dt;
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
     * 收盘价
     */
    BigDecimal closePrice;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 成交额
     */
    BigDecimal dealMoney;
}
