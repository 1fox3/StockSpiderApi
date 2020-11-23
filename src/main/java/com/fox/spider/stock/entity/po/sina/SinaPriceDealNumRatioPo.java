package com.fox.spider.stock.entity.po.sina;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 价格成交量占比数据
 * @author lusongsong
 * @date 2020/11/5 14:11
 */
@Data
public class SinaPriceDealNumRatioPo implements Serializable {
    /**
     * 价格
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
