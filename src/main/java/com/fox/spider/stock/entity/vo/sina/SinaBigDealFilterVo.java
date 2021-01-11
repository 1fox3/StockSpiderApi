package com.fox.spider.stock.entity.vo.sina;

import lombok.Data;

import java.io.Serializable;

/**
 * 新浪股票交易日大单交易筛选
 *
 * @author lusongsong
 * @date 2021/1/8 18:15
 */
@Data
public class SinaBigDealFilterVo implements Serializable {
    /**
     * 成交量
     */
    Integer dealNum = 0;
    /**
     * 成交金额
     */
    Integer dealMoney = 0;
    /**
     * 倍数
     */
    Integer times = 0;
}
