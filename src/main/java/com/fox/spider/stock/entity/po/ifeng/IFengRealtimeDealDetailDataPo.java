package com.fox.spider.stock.entity.po.ifeng;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 凤凰网交易明细详情信息
 *
 * @author lusongsong
 * @date 2021/1/7 17:51
 */
@Data
public class IFengRealtimeDealDetailDataPo implements Serializable {
    /**
     * 日期
     */
    String dt;
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
}
