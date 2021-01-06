package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.ifeng.IFengRealtimeDealInfoApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * 凤凰网股票接口测试类
 *
 * @author lusongsong
 * @date 2021/1/4 16:17
 */
@SpringBootTest
public class IFengApiTests extends StockBaseTests {
    /**
     * 凤凰网接口
     */
    @Autowired
    IFengRealtimeDealInfoApi iFengRealtimeDealInfoApi;

    /**
     * 按天成交信息测试
     */
    @Test
    void batchRealtimeDealInfoTest() {
        System.out.println(
                iFengRealtimeDealInfoApi.batchRealtimeDealInfo(
                        Arrays.asList(TEST_HK_STOCK)
                )
        );
    }
}
