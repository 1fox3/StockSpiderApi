package com.fox.spider.stock.api.sz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sz.SZKLineNodeDataPo;
import com.fox.spider.stock.entity.po.sz.SZKLinePo;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author lusongsong
 * @date 2021/1/12 17:22
 */
@Component
public class SZKLineApi extends SZBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     */
    private static final String API_URL = "http://www.szse.cn/api/market/ssjjhq/getHistoryData";

    /**
     * 获取日期类型参数值
     *
     * @param dtType
     * @return
     */
    private Integer getCycleType(Integer dtType) {
        switch (dtType) {
            case StockConst.DT_WEEK:
                return 32;
            case StockConst.DT_MONTH:
                return 34;
            default:
                return 33;
        }
    }

    /**
     * 获取K线数据
     *
     * @param stockVo
     * @param dtType
     * @return
     */
    public SZKLinePo kline(StockVo stockVo, Integer dtType) {
        if (!StockVo.verify(stockVo) || StockConst.SM_SZ != stockVo.getStockMarket()) {
            return null;
        }
        try {
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParam("cycleType", getCycleType(dtType))
                    .setParam("marketId", 1)
                    .setParam("code", stockVo.getStockCode());
            HttpResponseDto httpResponse = httpUtil.request();
            SZKLinePo szkLinePo = handleResponse(stockVo, httpResponse.getContent());
            szkLinePo.setKLineType(dtType);
            return szkLinePo;
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
    private SZKLinePo handleResponse(StockVo stockVo, String response) {
        if (!StockVo.verify(stockVo) || StringUtils.isEmpty(response)) {
            return null;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            jsonObject = jsonObject.containsKey("data") ? jsonObject.getJSONObject("data") : null;
            if (null != jsonObject && !jsonObject.isEmpty()) {
                SZKLinePo szkLinePo = new SZKLinePo();
                szkLinePo.setStockMarket(stockVo.getStockMarket());
                szkLinePo.setStockCode(stockVo.getStockCode());
                if (jsonObject.containsKey("name")) {
                    szkLinePo.setStockName(jsonObject.getString("name"));
                }
                if (jsonObject.containsKey("picupdata") && jsonObject.containsKey("picdowndata")) {
                    JSONArray kLineArr = jsonObject.getJSONArray("picupdata");
                    JSONArray dealKLineArr = jsonObject.getJSONArray("picdowndata");
                    if (!kLineArr.isEmpty()) {
                        List<SZKLineNodeDataPo> szkLineNodeDataPoList = new ArrayList<>(kLineArr.size());
                        for (int i = 0; i < kLineArr.size(); i++) {
                            JSONArray dayArr = kLineArr.getJSONArray(i);
                            JSONArray dealArr = dealKLineArr.getJSONArray(i);
                            if (!dayArr.isEmpty()) {
                                SZKLineNodeDataPo szkLineNodeDataPo = new SZKLineNodeDataPo();
                                for (int j = 0; j < dayArr.size(); j++) {
                                    String dayStr = dayArr.getString(j);
                                    if (0 == j) {
                                        szkLineNodeDataPo.setDt(dayStr);
                                    } else if (1 == j) {
                                        szkLineNodeDataPo.setOpenPrice(
                                                BigDecimalUtil.initPrice(dayStr)
                                        );
                                    } else if (2 == j) {
                                        szkLineNodeDataPo.setClosePrice(
                                                BigDecimalUtil.initPrice(dayStr)
                                        );
                                    } else if (3 == j) {
                                        szkLineNodeDataPo.setLowestPrice(
                                                BigDecimalUtil.initPrice(dayStr)
                                        );
                                    } else if (4 == j) {
                                        szkLineNodeDataPo.setHighestPrice(
                                                BigDecimalUtil.initPrice(dayStr)
                                        );
                                    } else if (5 == j) {
                                        szkLineNodeDataPo.setUptickPrice(
                                                BigDecimalUtil.initPrice(dayStr)
                                        );
                                    } else if (6 == j) {
                                        szkLineNodeDataPo.setUptickRate(
                                                BigDecimalUtil.initRate(dayStr)
                                        );
                                    } else if (7 == j) {
                                        szkLineNodeDataPo.setDealNum(
                                                BigDecimalUtil.initLong(dayStr, BigDecimalUtil.LONG_MULTIPLY_100)
                                        );
                                    } else if (8 == j) {
                                        szkLineNodeDataPo.setDealMoney(
                                                BigDecimalUtil.initPrice(dayStr)
                                        );
                                    }
                                }
                                szkLineNodeDataPo.setDealScale(dealArr.getString(2));
                                szkLineNodeDataPoList.add(szkLineNodeDataPo);
                            }
                        }
                        szkLinePo.setKlineData(szkLineNodeDataPoList);
                        szkLinePo.setNodeCount(szkLineNodeDataPoList.size());
                    }
                }
                return szkLinePo;
            }
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }
}
