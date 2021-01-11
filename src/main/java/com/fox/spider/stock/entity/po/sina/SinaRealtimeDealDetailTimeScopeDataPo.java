package com.fox.spider.stock.entity.po.sina;

import lombok.Data;

import java.io.Serializable;

/**
 * 新浪网股票最新交易日交易明细时间范围详情
 *
 * @author lusongsong
 * @date 2021/1/8 16:26
 */
@Data
public class SinaRealtimeDealDetailTimeScopeDataPo implements Serializable {
    /**
     * 页码
     */
    Integer pageNum;
    /**
     * 开始时间
     */
    String startTime;
    /**
     * 结束时间
     */
    String endTime;
}
