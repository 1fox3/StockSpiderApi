package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.sh.SHRealtimeDealInfoApi;
import com.fox.spider.stock.api.sh.SHRealtimeMinuteKLineApi;
import com.fox.spider.stock.api.sh.SHStockInfoApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 沪市交易所接口测试类
 *
 * @author lusongsong
 * @date 2020/11/20 14:15
 */
@SpringBootTest
public class SHApiTests extends StockBaseTests {
    /**
     * 沪市测试股票编码
     */
    private static final String TEST_SH_STOCK_CODE = TEST_SH_STOCK.getStockCode();
    /**
     * 沪市接口
     */
    @Autowired
    SHStockInfoApi shStockInfoApi;
    @Autowired
    SHRealtimeMinuteKLineApi shRealtimeMinuteKLineApi;
    @Autowired
    SHRealtimeDealInfoApi shRealtimeDealInfoApi;

    /**
     * 按天成交信息测试
     */
    @Test
    void stockInfoTest() {
        System.out.println(shStockInfoApi.stockInfo(TEST_SH_STOCK_CODE));
    }

    /**
     * 最新交易日分钟新图数据测试
     */
    @Test
    void realtimeMinuteKLineTest() {
        System.out.println(shRealtimeMinuteKLineApi.realtimeMinuteKLine(TEST_SH_STOCK));
    }

    /**
     * 最新交易日交易详情信息
     */
    @Test
    void realtimeDealInfoTest() {
        System.out.println(shRealtimeDealInfoApi.realtimeDealInfo(TEST_SH_STOCK));
    }
}
