package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.tencent.*;
import com.fox.spider.stock.constant.StockConst;
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

    @Test
    void kLineTest() {
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_DAY, StockConst.SFQ_NO,2));
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_DAY, StockConst.SFQ_BEFORE,2));
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_DAY, StockConst.SFQ_AFTER,2));
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_WEEK, StockConst.SFQ_NO,2));
        System.out.println(tencentKLineApi.kLine(TEST_HK_STOCK, StockConst.DT_MONTH, StockConst.SFQ_NO,2));
    }
}
