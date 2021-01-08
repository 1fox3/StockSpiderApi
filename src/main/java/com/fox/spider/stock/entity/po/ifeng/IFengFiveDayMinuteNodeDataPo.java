package com.fox.spider.stock.entity.po.ifeng;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 凤凰网5天分钟数据
 *
 * @author lusongsong
 * @date 2021/1/6 17:32
 */
@Data
public class IFengFiveDayMinuteNodeDataPo implements Serializable {
    /**
     * 时间
     */
    String time;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 涨跌幅
     */
    BigDecimal uptickRate;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 均价
     */
    BigDecimal avgPrice;
    /**
     * 量比
     */
    BigDecimal qrrRate;
}
