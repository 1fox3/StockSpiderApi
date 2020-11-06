package com.fox.spider.stock;

import com.fox.spider.stock.api.nets.NetsDayDealInfo;
import com.fox.spider.stock.api.nets.NetsFQKLineData;
import com.fox.spider.stock.api.nets.NetsFQTotalClosePrice;
import com.fox.spider.stock.api.nets.NetsRealtimeMinuteDealInfo;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
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
class NetsApiTests {
    @Autowired
    NetsDayDealInfo netsDayDealInfo;
    @Autowired
    NetsFQKLineData netsFQKLineData;
    @Autowired
    NetsRealtimeMinuteDealInfo netsRealtimeMinuteDealInfo;
    @Autowired
    NetsFQTotalClosePrice netsFQTotalClosePrice;
    /**
     * 股票实例
     */
    private StockVo stockVo = new StockVo("603383", StockConst.SM_SH);//顶点软件
//    private StockVo stockVo = new StockVo("300033", StockConst.SM_SZ);//同花顺
//    private StockVo stockVo = new StockVo("00700", StockConst.SM_HK);//腾讯

    /**
     * 按天成交信息测试
     */
    @Test
    void dayDealInfoTest() {
        String startDate = "2020-10-01";
        String endDate = "2020-11-01";
        System.out.println(netsDayDealInfo.dayDealInfo(stockVo, startDate, endDate));
    }

    /**
     * 复权类型成交信息测试
     */
    @Test
    void fqKLineDataTest() {
        String startDate = "2019-10-01";
        String endDate = "2020-11-01";
        System.out.println(netsFQKLineData.fqKLineData(stockVo, startDate, endDate, StockConst.SFQ_BEFORE));
    }

    /**
     * 实时交易分钟线图数据测试
     */
    @Test
    void realtimeMinuteDealInfoTest() {
        System.out.println(netsRealtimeMinuteDealInfo.realtimeMinuteKLine(stockVo));
    }

    /**
     * 实时交易分钟线图数据测试
     */
    @Test
    void netsFQTotalClosePriceTest() {
        System.out.println(netsFQTotalClosePrice.fqClosePrice(stockVo, StockConst.DT_DAY, StockConst.SFQ_BEFORE));
    }
}
