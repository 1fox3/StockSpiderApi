package com.fox.spider.stock.api.sina;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.vo.StockVo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新浪股票数据接口基类
 *
 * @author lusongsong
 * @date 2020/11/4 15:40
 */
public class SinaBaseApi {
    /**
     * 网易支持的股市列表
     */
    public static final List<Integer> SINA_SUPPORT_SM = Arrays.asList(
            StockConst.SM_SH,
            StockConst.SM_SZ,
            StockConst.SM_HK
    );
    /**
     * 股票交易所对应的拼音
     */
    public static Map<Integer, String> SINA_SM_PY_MAP = new HashMap<Integer, String>() {{
        put(StockConst.SM_SH, "sh");//沪
        put(StockConst.SM_SZ, "sz");//深
        put(StockConst.SM_HK, "hk");//港
    }};

    /**
     * 当日无交易的状态
     */
    public static final List<String> NO_DEAL_STATUS_LIST = Arrays.asList(
            "-3",
            "03",
            //新股未上市
            "-2",
            "07"
    );

    /**
     * 处理json字符串的key无双引号的问题
     *
     * @param jsonStr
     * @return
     */
    protected String handleJsonStr(String jsonStr) {
        return jsonStr.replaceAll("([a-zA-Z0-9_]+):", "\"$1\":");
    }

    /**
     * 获取股票集市对应的拼音
     *
     * @param stockMarket
     * @return
     */
    public static String sinaStockMarketPY(Integer stockMarket) {
        return SinaBaseApi.SINA_SM_PY_MAP.containsKey(stockMarket) ?
                SinaBaseApi.SINA_SM_PY_MAP.get(stockMarket) : "hs";
    }

    /**
     * 获取新浪股票代码
     *
     * @param stockVo
     * @return
     */
    public static String sinaStockCode(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockCode() || stockVo.getStockCode().isEmpty()
                || null == stockVo.getStockMarket() || !StockConst.SM_ALL.contains(stockVo.getStockMarket())) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SinaBaseApi.sinaStockMarketPY(stockVo.getStockMarket()));
        stringBuffer.append(stockVo.getStockCode());
        return stringBuffer.toString();
    }

    /**
     * 判断是够已被拒绝
     *
     * @param httpResponseDto
     * @return
     */
    public static Boolean isForbidden(HttpResponseDto httpResponseDto) {
        return 456 == httpResponseDto.getCode();
    }

    /**
     * 处理拒绝
     */
    public static void handleForbidden() {
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
        }
    }
}
