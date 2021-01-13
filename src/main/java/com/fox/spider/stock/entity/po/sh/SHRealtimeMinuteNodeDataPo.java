package com.fox.spider.stock.entity.po.sh;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分钟线图数据
 *
 * @author lusongsong
 * @date 2021/1/12 10:56
 */
@Data
public class SHRealtimeMinuteNodeDataPo implements Serializable {
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
     * 价格涨幅
     */
    BigDecimal uptickPrice;
    /**
     * 增长率
     */
    BigDecimal uptickRate;
}
