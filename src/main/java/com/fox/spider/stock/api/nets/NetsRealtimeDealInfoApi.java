package com.fox.spider.stock.api.nets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import com.fox.spider.stock.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 网易股票最新交易日交易数据
 *
 * @author lusongsong
 * @date 2021/1/18 17:51
 */
@Component
public class NetsRealtimeDealInfoApi extends NetsBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * http://api.money.126.net/data/feed/0603383
     */
    private static String API_URL = "http://api.money.126.net/data/feed/";
    /**
     * 股票代码拼接字符串
     */
    private static String STOCK_CODE_SPLIT_STR = ",";

    /**
     * 获取单只股票的实时交易数据
     *
     * @param stockVo
     * @return
     */
    public NetsRealtimeDealInfoPo realtimeDealInfo(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockCode() || null == stockVo.getStockMarket()) {
            return null;
        }
        List<StockVo> stockVoList = Arrays.asList(stockVo);
        Map<String, NetsRealtimeDealInfoPo> netsRealtimeDealInfoPoMap = this.batchRealtimeDealInfo(stockVoList);
        if (null != netsRealtimeDealInfoPoMap && netsRealtimeDealInfoPoMap.containsKey(stockVo.getStockCode())) {
            return netsRealtimeDealInfoPoMap.get(stockVo.getStockCode());
        }
        return null;
    }

    /**
     * 获取批量股票的实时交易数据
     *
     * @param stockVoList
     * @return
     */
    public Map<String, NetsRealtimeDealInfoPo> batchRealtimeDealInfo(List<StockVo> stockVoList) {
        if (null == stockVoList || stockVoList.isEmpty()) {
            return null;
        }

        try {
            List<String> netsStockCodeList = new ArrayList<>();
            for (StockVo stockVo : stockVoList) {
                String netsStockCode = NetsBaseApi.netsStockCode(stockVo);
                if (null != netsStockCode && !netsStockCode.isEmpty()) {
                    netsStockCodeList.add(netsStockCode);
                }
            }
            if (netsStockCodeList.isEmpty()) {
                return null;
            }
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(API_URL + StringUtils.join(netsStockCodeList, STOCK_CODE_SPLIT_STR))
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 处理接口返回
     *
     * @param response
     * @return
     */
    private Map<String, NetsRealtimeDealInfoPo> handleResponse(String response) {
        response = JSONUtil.objectStrTrim(response);
        if (null != response && !response.isEmpty()) {
            try {
                JSONObject jsonObject = JSON.parseObject(response);
                if (null == jsonObject || jsonObject.isEmpty()) {
                    return null;
                }
                Map<String, NetsRealtimeDealInfoPo> netsRealtimeDealInfoPoMap = new HashMap<>();
                for (String netsStockCode : jsonObject.keySet()) {
                    StockVo stockVo = NetsBaseApi.netsStockCodeToStockVo(netsStockCode);
                    NetsRealtimeDealInfoPo netsRealtimeDealInfoPo =
                            handleRealtimeDealInfo(jsonObject.getJSONObject(netsStockCode));
                    if (null != stockVo && null != netsRealtimeDealInfoPo) {
                        netsRealtimeDealInfoPo.setStockMarket(stockVo.getStockMarket());
                        netsRealtimeDealInfoPo.setStockCode(stockVo.getStockCode());
                        netsRealtimeDealInfoPoMap.put(stockVo.getStockCode(), netsRealtimeDealInfoPo);
                    }
                }
                return netsRealtimeDealInfoPoMap;
            } catch (JSONException e) {
                logger.error(response, e);
            }
        }
        return null;
    }

    /**
     * 处理股票最新交易日交易信息
     *
     * @param jsonObject
     * @return
     */
    private NetsRealtimeDealInfoPo handleRealtimeDealInfo(JSONObject jsonObject) {
        try {
            if (null == jsonObject || jsonObject.isEmpty()) {
                return null;
            }
            NetsRealtimeDealInfoPo netsRealtimeDealInfoPo = new NetsRealtimeDealInfoPo();
            if (jsonObject.containsKey("name")) {
                netsRealtimeDealInfoPo.setStockName(jsonObject.getString("name"));
            }
            if (jsonObject.containsKey("price")) {
                netsRealtimeDealInfoPo.setCurrentPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("price"))
                );
            }
            if (jsonObject.containsKey("open")) {
                netsRealtimeDealInfoPo.setOpenPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("open"))
                );
            }
            if (jsonObject.containsKey("high")) {
                netsRealtimeDealInfoPo.setHighestPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("high"))
                );
            }
            if (jsonObject.containsKey("low")) {
                netsRealtimeDealInfoPo.setLowestPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("low"))
                );
            }
            if (jsonObject.containsKey("yestclose")) {
                netsRealtimeDealInfoPo.setPreClosePrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("yestclose"))
                );
            }
            if (jsonObject.containsKey("updown")) {
                netsRealtimeDealInfoPo.setUptickPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("updown"))
                );
            }
            if (jsonObject.containsKey("percent")) {
                netsRealtimeDealInfoPo.setUptickRate(
                        BigDecimalUtil.initRate(jsonObject.getString("percent"), BigDecimalUtil.RATE_MULTIPLY_100)
                );
            }
            if (jsonObject.containsKey("update")) {
                netsRealtimeDealInfoPo.setDt(
                        DateUtil.dateStrFormatChange(jsonObject.getString("update"), DateUtil.TIME_FORMAT_4, DateUtil.DATE_FORMAT_1)
                );
                netsRealtimeDealInfoPo.setTime(
                        DateUtil.dateStrFormatChange(jsonObject.getString("update"), DateUtil.TIME_FORMAT_4, DateUtil.TIME_FORMAT_2)
                );
            }
            TreeMap<BigDecimal, Long> buyPriceMap = new TreeMap<>();
            TreeMap<BigDecimal, Long> sellPriceMap = new TreeMap<>();
            for (int i = 1; i <= 5; i++) {
                String buyPriceKey = "bid" + i;
                String buyVolKey = "bidvol" + i;
                String sellPriceKey = "ask" + i;
                String sellVolKey = "askvol" + i;
                if (jsonObject.containsKey(buyPriceKey) && jsonObject.containsKey(buyVolKey)) {
                    buyPriceMap.put(
                            BigDecimalUtil.initPrice(jsonObject.getString(buyPriceKey)),
                            BigDecimalUtil.initLong(jsonObject.getString(buyVolKey))
                    );
                }
                if (jsonObject.containsKey(sellPriceKey) && jsonObject.containsKey(sellVolKey)) {
                    sellPriceMap.put(
                            BigDecimalUtil.initPrice(jsonObject.getString(sellPriceKey)),
                            BigDecimalUtil.initLong(jsonObject.getString(sellVolKey))
                    );
                }
            }
            netsRealtimeDealInfoPo.setBuyPriceMap(buyPriceMap);
            netsRealtimeDealInfoPo.setSellPriceMap(buyPriceMap);
            if (jsonObject.containsKey("volume")) {
                netsRealtimeDealInfoPo.setDealNum(
                        BigDecimalUtil.initLong(jsonObject.getString("volume"))
                );
            }
            if (jsonObject.containsKey("turnover")) {
                netsRealtimeDealInfoPo.setDealMoney(
                        BigDecimalUtil.initPrice(jsonObject.getString("turnover"))
                );
            }
            return netsRealtimeDealInfoPo;
        } catch (Exception e) {
            logger.error(jsonObject.toJSONString(), e);
        }
        return null;
    }
}
