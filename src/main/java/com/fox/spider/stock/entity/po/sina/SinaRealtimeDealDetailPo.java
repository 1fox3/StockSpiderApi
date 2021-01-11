package com.fox.spider.stock.entity.po.sina;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 新浪网交易明细数据
 *
 * @author lusongsong
 * @date 2021/1/8 16:57
 */
@Data
public class SinaRealtimeDealDetailPo implements Serializable {
    /**
     * 时间
     */
    String time;
    /**
     * 成交价格
     */
    BigDecimal price;
    /**
     * 涨跌幅
     */
    BigDecimal uptickRate;
    /**
     * 价格变动
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
     * 交易类型
     */
    Integer dealType;
}
