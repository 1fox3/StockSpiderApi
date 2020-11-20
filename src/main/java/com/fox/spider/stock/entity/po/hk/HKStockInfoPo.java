package com.fox.spider.stock.entity.po.hk;

import com.fox.spider.stock.constant.StockConst;
import lombok.Data;

/**
 * 港股股票信息
 *
 * @author lusongsong
 * @date 2020/11/20 14:57
 */
@Data
public class HKStockInfoPo {
    /**
     * 股票所属交易所
     */
    Integer stockMarket = StockConst.SM_HK;
    /**
     * 股票代码
     */
    String stockCode = "";
    /**
     * 股票名称
     */
    String stockName = "";
    /**
     * 股票全称
     */
    String stockFullName = "";
    /**
     * 公司法人代表
     */
    String stockLegal = "";
    /**
     * 股票公司注册地址
     */
    String stockRegisterAddress = "";
    /**
     * 股票公司通讯地址
     */
    String stockConnectAddress = "";
    /**
     * 股票上市日期
     */
    String stockOnDate = "";
    /**
     * 股票总股本(万)
     */
    Double stockTotalEquity = 0.0;
    /**
     * 股票流通股本(万)
     */
    Double stockCircEquity = 0.0;
    /**
     * 股票所属行业
     */
    String stockIndustry = "";
    /**
     * 股票CSRC行业(门类/大类/中类)
     */
    String stockCarc = "";
}
