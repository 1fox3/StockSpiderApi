package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.tencent.TencentRealtimeDealInfoApi;
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
}
