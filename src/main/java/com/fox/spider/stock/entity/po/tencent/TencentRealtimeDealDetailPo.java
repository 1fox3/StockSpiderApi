package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易明细数据
 *
 * @author lusongsong
 * @date 2020/12/29 15:37
 */
@Data
public class TencentRealtimeDealDetailPo implements Serializable {
    /**
     * 序号
     */
    Integer num;
    /**
     * 时间
     */
    String time;
    /**
     * 成交价格
     */
    BigDecimal price;
    /**
     * 价格差异
     */
    BigDecimal uptickPrice;
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
