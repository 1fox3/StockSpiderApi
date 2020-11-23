package com.fox.spider.stock.entity.po.nets;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 网易复权类型k线数据
 *
 * @author lusongsong
 * @date 2020/11/6 14:42
 */
@Data
public class NetsFQKLineDataPo implements Serializable {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 线图信息
     */
    List<NetsFQKLineNodeDataPo> klineData;
}
