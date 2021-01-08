package com.fox.spider.stock.entity.po.ifeng;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lusongsong
 * @date 2021/1/7 18:30
 */
@Data
public class IFengRealtimePriceDealNumPo implements Serializable {
    /**
     * 成交价
     */
    BigDecimal price;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 占比
     */
    BigDecimal ratio;
}
