package com.fox.spider.stock.entity.po.sz;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lusongsong
 * @date 2021/1/12 16:18
 */
@Data
public class SZRealtimeMinuteKLinePo implements Serializable {
    /**
     * 时间
     */
    String time;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 均价
     */
    BigDecimal avgPrice;
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
