package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 大单交易信息
 * @author lusongsong
 * @date 2020/12/31 15:49
 */
@Data
public class TencentBigDealPo implements Serializable {
    /**
     * 时间
     */
    String time;
    /**
     * 价格
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
}
