package com.fox.spider.stock.api.sh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sh.SHRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.po.sh.SHRealtimeMinuteNodeDataPo;
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
 * 上证交易所分钟粒度线图接口
 *
 * @author lusongsong
 * @date 2021/1/12 10:42
 */
@Component
public class SHRealtimeMinuteKLineApi extends SHBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     */
    private static final String API_URL = "http://yunhq.sse.com.cn:32041//v1/sh1/line/";

    /**
     * 获取最新交易日分钟线图信息
     *
     * @param stockVo
     * @return
     */
    public SHRealtimeMinuteKLinePo realtimeMinuteKLine(StockVo stockVo) {
        if (!StockVo.verify(stockVo) || StockConst.SM_SH != stockVo.getStockMarket()) {
            return null;
        }
        try {
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL + stockVo.getStockCode())
                    .setParam("begin", 0)
                    .setParam("end", -1)
                    .setParam("select", "time,price,volume,amount,change,chg_rate")
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
     * 处理接口返回
     *
     * @param stockVo
     * @param response
     * @return
     */
    private SHRealtimeMinuteKLinePo handleResponse(StockVo stockVo, String response) {
        if (!StockVo.verify(stockVo) || StringUtils.isEmpty(response)) {
            return null;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            if (!jsonObject.isEmpty()) {
                SHRealtimeMinuteKLinePo shRealtimeMinuteKLinePo = new SHRealtimeMinuteKLinePo();
                shRealtimeMinuteKLinePo.setStockMarket(stockVo.getStockMarket());
                shRealtimeMinuteKLinePo.setStockCode(stockVo.getStockCode());
                if (jsonObject.containsKey("date")) {
                    shRealtimeMinuteKLinePo.setDt(jsonObject.getString("date"));
                }
                if (jsonObject.containsKey("time")) {
                    shRealtimeMinuteKLinePo.setTime(
                            DateUtil.dateStrFormatChange(
                                    jsonObject.getString("time"),
                                    DateUtil.TIME_FORMAT_9,
                                    DateUtil.TIME_FORMAT_2
                            )
                    );
                }
                if (jsonObject.containsKey("prev_close")) {
                    shRealtimeMinuteKLinePo.setPreClosePrice(
                            BigDecimalUtil.initPrice(jsonObject.getString("prev_close"))
                    );
                }
                if (jsonObject.containsKey("highest")) {
                    shRealtimeMinuteKLinePo.setHighestPrice(
                            BigDecimalUtil.initPrice(jsonObject.getString("highest"))
                    );
                }
                if (jsonObject.containsKey("lowest")) {
                    shRealtimeMinuteKLinePo.setLowestPrice(
                            BigDecimalUtil.initPrice(jsonObject.getString("lowest"))
                    );
                }
                if (jsonObject.containsKey("total")) {
                    shRealtimeMinuteKLinePo.setNodeCount(jsonObject.getInteger("total"));
                }
                if (jsonObject.containsKey("line")) {
                    JSONArray line = jsonObject.getJSONArray("line");
                    shRealtimeMinuteKLinePo.setKlineData(handleNode(line));
                }
                return shRealtimeMinuteKLinePo;
            }
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 处理分钟线图数据
     *
     * @param jsonArray
     * @return
     */
    private List<SHRealtimeMinuteNodeDataPo> handleNode(JSONArray jsonArray) {
        if (jsonArray instanceof JSONArray && !jsonArray.isEmpty()) {
            List<SHRealtimeMinuteNodeDataPo> shRealtimeMinuteNodeDataPoList = new ArrayList<>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray nodeArr = jsonArray.getJSONArray(i);
                if (nodeArr instanceof JSONArray && !nodeArr.isEmpty()) {
                    SHRealtimeMinuteNodeDataPo shRealtimeMinuteNodeDataPo = new SHRealtimeMinuteNodeDataPo();
                    for (int j = 0; j < nodeArr.size(); j++) {
                        if (0 == j) {
                            shRealtimeMinuteNodeDataPo.setTime(
                                    DateUtil.dateStrFormatChange(
                                            nodeArr.getString(j),
                                            DateUtil.TIME_FORMAT_9,
                                            DateUtil.TIME_FORMAT_2
                                    )
                            );
                        } else if (1 == j) {
                            shRealtimeMinuteNodeDataPo.setPrice(
                                    BigDecimalUtil.initPrice(nodeArr.getString(j))
                            );
                        } else if (2 == j) {
                            shRealtimeMinuteNodeDataPo.setDealNum(
                                    BigDecimalUtil.initLong(nodeArr.getString(j))
                            );
                        } else if (3 == j) {
                            shRealtimeMinuteNodeDataPo.setDealMoney(
                                    BigDecimalUtil.initPrice(nodeArr.getString(j))
                            );
                        } else if (4 == j) {
                            shRealtimeMinuteNodeDataPo.setUptickPrice(
                                    BigDecimalUtil.initPrice(nodeArr.getString(j))
                            );
                        } else if (5 == j) {
                            shRealtimeMinuteNodeDataPo.setUptickRate(
                                    BigDecimalUtil.initRate(nodeArr.getString(j))
                            );
                        }
                    }
                    shRealtimeMinuteNodeDataPoList.add(shRealtimeMinuteNodeDataPo);
                }
            }
            return shRealtimeMinuteNodeDataPoList;
        }
        return null;
    }
}
