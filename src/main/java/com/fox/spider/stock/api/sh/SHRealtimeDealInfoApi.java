package com.fox.spider.stock.api.sh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sh.SHRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author lusongsong
 * @date 2021/1/12 11:24
 */
@Component
public class SHRealtimeDealInfoApi extends SHBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     */
    private static final String API_URL = "http://yunhq.sse.com.cn:32041//v1/sh1/snap/";

    /**
     * 获取最新交易日分钟线图信息
     *
     * @param stockVo
     * @return
     */
    public SHRealtimeDealInfoPo realtimeDealInfo(StockVo stockVo) {
        if (!StockVo.verify(stockVo) || StockConst.SM_SH != stockVo.getStockMarket()) {
            return null;
        }
        try {
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL + stockVo.getStockCode())
                    .setParam("select", "time,name,last,open,high,low,prev_close,up_limit,down_limit,change,chg_rate,volume,amount,ask,bid,tradephase,cpxxprodusta,cpxxlmttype,fp_phase,tradephase,hlt_tag,gdr_ratio,gdr_prevpx,gdr_currency")
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            return handleResponse(stockVo, httpResponse.getContent());
        } catch (Exception e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 处理返回数据
     *
     * @param stockVo
     * @param response
     * @return
     */
    private SHRealtimeDealInfoPo handleResponse(StockVo stockVo, String response) {
        if (!StockVo.verify(stockVo) || StringUtils.isEmpty(response)) {
            return null;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            if (!jsonObject.isEmpty()) {
                SHRealtimeDealInfoPo shRealtimeDealInfoPo = new SHRealtimeDealInfoPo();
                shRealtimeDealInfoPo.setStockMarket(stockVo.getStockMarket());
                shRealtimeDealInfoPo.setStockCode(stockVo.getStockCode());
                if (jsonObject.containsKey("date")) {
                    shRealtimeDealInfoPo.setDt(jsonObject.getString("date"));
                }
                if (jsonObject.containsKey("time")) {
                    shRealtimeDealInfoPo.setTime(
                            DateUtil.dateStrFormatChange(
                                    jsonObject.getString("time"),
                                    DateUtil.TIME_FORMAT_9,
                                    DateUtil.TIME_FORMAT_2
                            )
                    );
                }
                if (jsonObject.containsKey("snap")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("snap");
                    if (!jsonArray.isEmpty()) {
                        List<String> unknownList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            String infoStr = jsonArray.getString(i);
                            if (0 == i) {
                                shRealtimeDealInfoPo.setTime(
                                        DateUtil.dateStrFormatChange(
                                                infoStr,
                                                DateUtil.TIME_FORMAT_9,
                                                DateUtil.TIME_FORMAT_2
                                        )
                                );
                            } else if (1 == i) {
                                shRealtimeDealInfoPo.setStockName(infoStr);
                            } else if (2 == i) {
                                shRealtimeDealInfoPo.setCurrentPrice(BigDecimalUtil.initPrice(infoStr));
                            } else if (3 == i) {
                                shRealtimeDealInfoPo.setOpenPrice(BigDecimalUtil.initPrice(infoStr));
                            } else if (4 == i) {
                                shRealtimeDealInfoPo.setHighestPrice(BigDecimalUtil.initPrice(infoStr));
                            } else if (5 == i) {
                                shRealtimeDealInfoPo.setLowestPrice(BigDecimalUtil.initPrice(infoStr));
                            } else if (6 == i) {
                                shRealtimeDealInfoPo.setPreClosePrice(BigDecimalUtil.initPrice(infoStr));
                            } else if (7 == i) {
                                shRealtimeDealInfoPo.setUpLimitPrice(BigDecimalUtil.initPrice(infoStr));
                            } else if (8 == i) {
                                shRealtimeDealInfoPo.setDownLimitPrice(BigDecimalUtil.initPrice(infoStr));
                            } else if (9 == i) {
                                shRealtimeDealInfoPo.setUptickPrice(BigDecimalUtil.initPrice(infoStr));
                            } else if (10 == i) {
                                shRealtimeDealInfoPo.setUptickRate(BigDecimalUtil.initPrice(infoStr));
                            } else if (11 == i) {
                                shRealtimeDealInfoPo.setDealNum(BigDecimalUtil.initLong(infoStr));
                            } else if (12 == i) {
                                shRealtimeDealInfoPo.setDealMoney(BigDecimalUtil.initPrice(infoStr));
                            } else if (13 == i || 14 == i) {
                                LinkedHashMap<BigDecimal, Long> priceMap = new LinkedHashMap(5);
                                JSONArray priceArr = jsonArray.getJSONArray(i);
                                Long num = null;
                                BigDecimal price = null;
                                for (int j = 0; j < priceArr.size(); j++) {
                                    if (0 == j % 2) {
                                        price = BigDecimalUtil.initPrice(priceArr.getString(j));
                                    } else {
                                        num = BigDecimalUtil.initLong(priceArr.getString(j));
                                        priceMap.put(price, num);
                                    }
                                }
                                if (13 == i) {
                                    shRealtimeDealInfoPo.setSellPriceMap(priceMap);
                                } else {
                                    shRealtimeDealInfoPo.setBuyPriceMap(priceMap);
                                }
                            } else {
                                unknownList.add(infoStr);
                            }
                        }
                        shRealtimeDealInfoPo.setUnknownKeyList(unknownList);
                    }
                }
                return shRealtimeDealInfoPo;
            }
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }
}
