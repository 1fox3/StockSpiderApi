package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.tencent.*;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.PageVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * 腾讯接口测试类
 *
 * @author lusongsong
 * @date 2020/12/24 14:09
 */
@SpringBootTest
public class TencentApiTests extends StockBaseTests {
    /**
     * 腾讯接口
     */
    @Autowired
    TencentRealtimeDealInfoApi tencentRealtimeDealInfoApi;
    @Autowired
    TencentRealtimeMinuteKLineApi tencentRealtimeMinuteKLineApi;
    @Autowired
    TencentFiveDayMinuteKLineApi tencentFiveDayMinuteKLineApi;
    @Autowired
    TencentMinuteScopeKLineApi tencentMinuteScopeKLineApi;
    @Autowired
    TencentKLineApi tencentKLineApi;
    @Autowired
    TencentRealtimeDealDetailTimeScopeApi tencentRealtimeDealDetailTimeScopeApi;
    @Autowired
    TencentRealtimeDealDetailApi tencentRealtimeDealDetailApi;
    @Autowired
    TencentRealtimePriceDealNumApi tencentRealtimePriceDealNumApi;
    @Autowired
    TencentRelateStockApi tencentRelateStockApi;
    @Autowired
    TencentRelateBlockApi tencentRelateBlockApi;
    @Autowired
    TencentBigDealApi tencentBigDealApi;

    /**
     * 实时交易数据测试
     */
    @Test
    void realtimeDealInfoTest() {
        System.out.println(tencentRealtimeDealInfoApi.batchRealtimeDealInfo(
                Arrays.asList(
                        TEST_SH_STOCK,
                        TEST_SZ_STOCK,
                        TEST_HK_STOCK
                ))
        );
    }

    /**
     * 实时交易分钟线图测试
     */
    @Test
    void realtimeMinuteKLineTest() {
        System.out.println(tencentRealtimeMinuteKLineApi.realtimeMinuteKLine(TEST_SH_STOCK));
    }

    /**
     * 腾讯股票5日分钟线图测试
     */
    @Test
    void fiveDayMinKLineTest() {
        System.out.println(tencentFiveDayMinuteKLineApi.fiveDayKLine(TEST_SH_STOCK));
    }

    /**
     * 腾讯股票不同分钟粒度线图测试
     */
    @Test
    void minuteScopeKLineTest() {
        System.out.println(tencentMinuteScopeKLineApi.minuteScopeKLine(TEST_SH_STOCK, 5, 200));
    }

    /**
     * 腾讯股票K线图测试
     */
    @Test
    void kLineTest() {
        String startDate = "2021-05-25";
        String endDate = "2021-05-20";
//        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_DAY, StockConst.SFQ_NO, startDate, endDate));
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_DAY, StockConst.SFQ_BEFORE, startDate, endDate));
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_DAY, StockConst.SFQ_AFTER, startDate, endDate));
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_WEEK, StockConst.SFQ_BEFORE, startDate, endDate));
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_MONTH, StockConst.SFQ_BEFORE, startDate, endDate));
    }

    /**
     * 交易明细测试
     */
    @Test
    void dealDetailTimeScopeTest() {
        System.out.println(tencentRealtimeDealDetailTimeScopeApi.dealDetailTimeScope(TEST_SH_STOCK));
    }

    /**
     * 交易明细测试
     */
    @Test
    void dealDetailTest() {
        System.out.println(tencentRealtimeDealDetailApi.dealDetail(TEST_SH_STOCK, 0));
    }

    /**
     * 价格成交量测试
     */
    @Test
    void priceDealNumTest() {
        System.out.println(tencentRealtimePriceDealNumApi.priceDealNum(TEST_SH_STOCK));
    }

    /**
     * 相关股票测试
     */
    @Test
    void relateStockTest() {
        System.out.println(tencentRelateStockApi.relateStock(TEST_SH_STOCK));
    }

    /**
     * 股票所属板块
     */
    @Test
    void relateBlockTest() {
        System.out.println(tencentRelateBlockApi.relateBlock(TEST_SH_STOCK));
    }

    /**
     * 大单交易信息
     */
    @Test
    void bigDealTest() {
        System.out.println(tencentBigDealApi.bigDeal(
                TEST_SH_STOCK, 1, 0, new PageVo(1, 10))
        );
    }
}
