package com.fox.spider.stock;

import com.fox.spider.stock.api.sz.SZStockInfoApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 深市交易所接口测试类
 *
 * @author lusongsong
 * @date 2020/11/20 14:38
 */
@SpringBootTest
public class SZApiTests extends StockApiBaseTests {
    /**
     * 深市测试股票编码
     */
    private static final String TEST_SZ_STOCK_CODE = TEST_SZ_STOCK.getStockCode();
    /**
     * 深市接口
     */
    @Autowired
    SZStockInfoApi szStockInfoApi;

    /**
     * 按天成交信息测试
     */
    @Test
    void stockInfoTest() {
        System.out.println(szStockInfoApi.stockInfo(TEST_SZ_STOCK_CODE));
    }
}
