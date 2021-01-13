package com.fox.spider.stock.api.sz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sh.SHRealtimeDealInfoPo;
import com.fox.spider.stock.entity.po.sz.SZRealtimeDealPo;
import com.fox.spider.stock.entity.po.sz.SZRealtimeMinuteKLinePo;
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
 * 深证股票最新交易日交易数据
 *
 * @author lusongsong
 * @date 2021/1/12 15:26
 */
@Component
public class SZRealtimeDealApi extends SZBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     */
    private static final String API_URL = "http://www.szse.cn/api/market/ssjjhq/getTimeData";

    /**
     * 获取实时交易信息
     *
     * @param stockVo
     * @return
     */
    public SZRealtimeDealPo realtimeDeal(StockVo stockVo) {
        if (!StockVo.verify(stockVo) || StockConst.SM_SZ != stockVo.getStockMarket()) {
            return null;
        }
        try {
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParam("marketId", 1)
                    .setParam("code", stockVo.getStockCode());
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
    private SZRealtimeDealPo handleResponse(StockVo stockVo, String response) {
        if (!StockVo.verify(stockVo) || StringUtils.isEmpty(response)) {
            return null;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            jsonObject = jsonObject.containsKey("data") ? jsonObject.getJSONObject("data") : null;
            if (null != jsonObject && !jsonObject.isEmpty()) {
                SZRealtimeDealPo szRealtimeDealPo = handleRealtimeDealInfo(stockVo, jsonObject);
                if (null != szRealtimeDealPo) {
                    szRealtimeDealPo.setMinuteKLine(handleMinuteKLine(stockVo, jsonObject));
                }
                return szRealtimeDealPo;
            }
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 处理最新交易日交易数据
     *
     * @param stockVo
     * @param jsonObject
     * @return
     */
    private SZRealtimeDealPo handleRealtimeDealInfo(StockVo stockVo, JSONObject jsonObject) {
        if (!StockVo.verify(stockVo) || null == jsonObject || jsonObject.isEmpty()) {
            return null;
        }
        try {
            SZRealtimeDealPo szRealtimeDealPo = new SZRealtimeDealPo();
            szRealtimeDealPo.setStockMarket(stockVo.getStockMarket());
            szRealtimeDealPo.setStockCode(stockVo.getStockCode());
            if (jsonObject.containsKey("name")) {
                szRealtimeDealPo.setStockName(jsonObject.getString("name"));
            }
            if (jsonObject.containsKey("marketTime")) {
                String marketTime = jsonObject.getString("marketTime");
                szRealtimeDealPo.setDt(
                        DateUtil.dateStrFormatChange(
                                marketTime,
                                DateUtil.TIME_FORMAT_1,
                                DateUtil.DATE_FORMAT_1
                        )
                );
                szRealtimeDealPo.setTime(
                        DateUtil.dateStrFormatChange(
                                marketTime,
                                DateUtil.TIME_FORMAT_1,
                                DateUtil.TIME_FORMAT_2
                        )
                );
            }
            if (jsonObject.containsKey("now")) {
                szRealtimeDealPo.setCurrentPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("now"))
                );
            }
            if (jsonObject.containsKey("open")) {
                szRealtimeDealPo.setOpenPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("open"))
                );
            }
            if (jsonObject.containsKey("high")) {
                szRealtimeDealPo.setHighestPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("high"))
                );
            }
            if (jsonObject.containsKey("low")) {
                szRealtimeDealPo.setLowestPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("low"))
                );
            }
            if (jsonObject.containsKey("close")) {
                szRealtimeDealPo.setPreClosePrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("close"))
                );
            }
            if (jsonObject.containsKey("delta")) {
                szRealtimeDealPo.setUptickPrice(
                        BigDecimalUtil.initPrice(jsonObject.getString("delta"))
                );
            }
            if (jsonObject.containsKey("deltaPercent")) {
                szRealtimeDealPo.setUptickRate(
                        BigDecimalUtil.initRate(jsonObject.getString("deltaPercent"))
                );
            }
            if (jsonObject.containsKey("volume")) {
                szRealtimeDealPo.setDealNum(
                        BigDecimalUtil.initLong(jsonObject.getString("volume"), BigDecimalUtil.LONG_MULTIPLY_100)
                );
            }
            if (jsonObject.containsKey("amount")) {
                szRealtimeDealPo.setDealMoney(
                        BigDecimalUtil.initPrice(jsonObject.getString("amount"))
                );
            }
            if (jsonObject.containsKey("sellbuy5")) {
                JSONArray priceArr = jsonObject.getJSONArray("sellbuy5");
                if (priceArr.size() == 10) {
                    LinkedHashMap<BigDecimal, Long> priceMap = new LinkedHashMap<>(5);
                    for (int i = 0; i < priceArr.size(); i++) {
                        JSONObject priceObj = priceArr.getJSONObject(i);
                        if (!priceObj.isEmpty() && priceObj.containsKey("price") && priceObj.containsKey("volume")) {
                            priceMap.put(
                                    BigDecimalUtil.initPrice(priceObj.getString("price")),
                                    BigDecimalUtil.initLong(priceObj.getString("volume"), BigDecimalUtil.LONG_MULTIPLY_100)
                            );
                        }
                        if (5 == i) {
                            szRealtimeDealPo.setSellPriceMap(priceMap);
                            priceMap = new LinkedHashMap<>(5);
                        }
                    }
                    szRealtimeDealPo.setBuyPriceMap(priceMap);
                }
            }
            return szRealtimeDealPo;
        } catch (JSONException e) {
            logger.error(jsonObject.toJSONString(), e);
        }
        return null;
    }

    /**
     * 处理最新交易日分钟线图数据
     *
     * @param stockVo
     * @param jsonObject
     * @return
     */
    private List<SZRealtimeMinuteKLinePo> handleMinuteKLine(StockVo stockVo, JSONObject jsonObject) {
        if (!StockVo.verify(stockVo) || null == jsonObject || jsonObject.isEmpty()) {
            return null;
        }
        try {
            if (jsonObject.containsKey("picupdata") && jsonObject.containsKey("picdowndata")) {
                JSONArray minuteKLineArr = jsonObject.getJSONArray("picupdata");
                JSONArray dealKLineArr = jsonObject.getJSONArray("picdowndata");
                if (!minuteKLineArr.isEmpty()) {
                    List<SZRealtimeMinuteKLinePo> szRealtimeMinuteKLinePoList = new ArrayList<>(minuteKLineArr.size());
                    for (int i = 0; i < minuteKLineArr.size(); i++) {
                        JSONArray minuteArr = minuteKLineArr.getJSONArray(i);
                        JSONArray dealArr = dealKLineArr.getJSONArray(i);
                        if (!minuteArr.isEmpty()) {
                            SZRealtimeMinuteKLinePo szRealtimeMinuteKLinePo = new SZRealtimeMinuteKLinePo();
                            for (int j = 0; j < minuteArr.size(); j++) {
                                String minuteStr = minuteArr.getString(j);
                                if (0 == j) {
                                    szRealtimeMinuteKLinePo.setTime(
                                            DateUtil.dateStrFormatChange(
                                                    minuteStr,
                                                    DateUtil.TIME_FORMAT_6,
                                                    DateUtil.TIME_FORMAT_2
                                            )
                                    );
                                } else if (1 == j) {
                                    szRealtimeMinuteKLinePo.setPrice(
                                            BigDecimalUtil.initPrice(minuteStr)
                                    );
                                } else if (2 == j) {
                                    szRealtimeMinuteKLinePo.setAvgPrice(
                                            BigDecimalUtil.initPrice(minuteStr)
                                    );
                                } else if (3 == j) {
                                    szRealtimeMinuteKLinePo.setUptickPrice(
                                            BigDecimalUtil.initPrice(minuteStr)
                                    );
                                } else if (4 == j) {
                                    szRealtimeMinuteKLinePo.setUptickRate(
                                            BigDecimalUtil.initRate(minuteStr)
                                    );
                                } else if (5 == j) {
                                    szRealtimeMinuteKLinePo.setDealNum(
                                            BigDecimalUtil.initLong(minuteStr, BigDecimalUtil.LONG_MULTIPLY_100)
                                    );
                                } else if (6 == j) {
                                    szRealtimeMinuteKLinePo.setDealMoney(
                                            BigDecimalUtil.initPrice(minuteStr)
                                    );
                                }
                            }
                            szRealtimeMinuteKLinePo.setDealScale(dealArr.getString(2));
                            szRealtimeMinuteKLinePoList.add(szRealtimeMinuteKLinePo);
                        }
                    }
                    return szRealtimeMinuteKLinePoList;
                }
            }
        } catch (JSONException e) {
            logger.error(jsonObject.toJSONString(), e);
        }
        return null;
    }
}
