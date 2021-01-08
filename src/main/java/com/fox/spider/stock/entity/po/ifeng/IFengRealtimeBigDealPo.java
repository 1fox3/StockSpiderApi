package com.fox.spider.stock.entity.po.ifeng;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 凤凰网大单交易
 *
 * @author lusongsong
 * @date 2021/1/7 15:29
 */
@Data
public class IFengRealtimeBigDealPo implements Serializable {
    /**
     * 时间
     */
    String time;
    /**
     * 成交价
     */
    BigDecimal price;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 性质
     */
    Integer dealType;
    /**
     * 价格涨幅
     */
    BigDecimal uptickPrice;
    /**
     * 增长率
     */
    BigDecimal uptickRate;
}
