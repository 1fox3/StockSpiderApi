package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.nets.*;
import com.fox.spider.stock.constant.StockConst;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 网易接口测试类
 *
 * @author lusongsong
 * @date 2020/11/6 14:07
 */
@SpringBootTest
class NetsApiTests extends StockBaseTests {
    /**
     * 网易接口
     */
    @Autowired
    NetsDayDealInfoApi netsDayDealInfoApi;
    @Autowired
    NetsFQKLineDataApi netsFQKLineDataApi;
    @Autowired
    NetsRealtimeMinuteKLineApi netsRealtimeMinuteKLineApi;
    @Autowired
    NetsFQTotalClosePriceApi netsFQTotalClosePriceApi;
    @Autowired
    NetsRealtimeDealInfoApi netsRealtimeDealInfoApi;

    /**
     * 网易股票最新交易日交易数据接口测试
     */
    @Test
    void realtimeDealInfoTest() {
        System.out.println(netsRealtimeDealInfoApi.realtimeDealInfo(TEST_SH_STOCK));
    }

    /**
     * 按天成交信息测试
     */
    @Test
    void dayDealInfoTest() {
        String startDate = "2020-10-01";
        String endDate = "2020-11-01";
        System.out.println(netsDayDealInfoApi.dayDealInfo(TEST_SH_STOCK, startDate, endDate));
    }

    /**
     * 复权类型成交信息测试
     */
    @Test
    void fqKLineDataTest() {
        String startDate = "2019-10-01";
        String endDate = "2020-11-01";
        System.out.println(netsFQKLineDataApi.fqKLineData(TEST_SH_STOCK, startDate, endDate, StockConst.SFQ_BEFORE));
    }

    /**
     * 实时交易分钟线图数据测试
     */
    @Test
    void realtimeMinuteKLineTest() {
        System.out.println(netsRealtimeMinuteKLineApi.realtimeMinuteKLine(TEST_SH_STOCK));
    }

    /**
     * 实时交易分钟线图数据测试
     */
    @Test
    void netsFQTotalClosePriceTest() {
        System.out.println(netsFQTotalClosePriceApi.fqClosePrice(TEST_SH_STOCK, StockConst.DT_DAY, StockConst.SFQ_BEFORE));
    }
}
