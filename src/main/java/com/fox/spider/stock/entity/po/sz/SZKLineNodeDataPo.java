package com.fox.spider.stock.entity.po.sz;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 深证股票k线数据
 *
 * @author lusongsong
 * @date 2021/1/12 17:24
 */
@Data
public class SZKLineNodeDataPo implements Serializable {
    /**
     * 日期
     */
    String dt;
    /**
     * 开盘价格
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
     * 价格涨幅
     */
    BigDecimal uptickPrice;
    /**
     * 增长率
     */
    BigDecimal uptickRate;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 交易规模
     */
    String dealScale;
}
