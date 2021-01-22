package com.fox.spider.stock.api.ifeng;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.ifeng.IFengFiveDayMinuteKLinePo;
import com.fox.spider.stock.entity.po.ifeng.IFengFiveDayMinuteNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 凤凰网5日分钟线图数据
 *
 * @author lusongsong
 * @date 2021/1/6 16:40
 */
@Component
public class IFengFiveDayMinuteKLineApi extends IFengBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://api.finance.ifeng.com/aminhis/?code=sz002475&type=five
     */
    private static final String API_URL = "https://api.finance.ifeng.com/aminhis/";

    /**
     * 获取股票5日分钟线图
     *
     * @param stockVo
     * @return
     */
    public List<IFengFiveDayMinuteKLinePo> fiveDayKLine(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String iFengStockCode = iFengStockCode(stockVo);
            Map<String, Object> params = new HashMap<>(2);
            params.put("code", iFengStockCode);
            params.put("type", "five");
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            List<IFengFiveDayMinuteKLinePo> iFengFiveDayMinuteKLinePoList = this.handleResponse(
                    stockVo, httpResponse.getContent()
            );
            return iFengFiveDayMinuteKLinePoList;
        } catch (IOException e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 解析返回数据
     *
     * @param stockVo
     * @param response
     * @return
     */
    private List<IFengFiveDayMinuteKLinePo> handleResponse(StockVo stockVo, String response) {
        if (null == stockVo || null == response || response.isEmpty()) {
            return null;
        }
        try {
            JSONArray jsonArray = JSON.parseArray(response);
            if (null == jsonArray || jsonArray.isEmpty()) {
                return null;
            }
            List<IFengFiveDayMinuteKLinePo> iFengFiveDayMinuteKLinePoList = null;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (null == jsonObject || jsonObject.isEmpty()) {
                    continue;
                }
                IFengFiveDayMinuteKLinePo iFengFiveDayMinuteKLinePo = new IFengFiveDayMinuteKLinePo();
                iFengFiveDayMinuteKLinePo.setStockMarket(stockVo.getStockMarket());
                iFengFiveDayMinuteKLinePo.setStockCode(stockVo.getStockCode());
                if (jsonObject.containsKey("1")) {
                    iFengFiveDayMinuteKLinePo.setPreClosePrice(
                            BigDecimalUtil.initPrice(jsonObject.getString("1"))
                    );
                }
                if (jsonObject.containsKey("2")) {
                    iFengFiveDayMinuteKLinePo.setOpenPrice(
                            BigDecimalUtil.initPrice(jsonObject.getString("2"))
                    );
                }
                if (jsonObject.containsKey("3")) {
                    iFengFiveDayMinuteKLinePo.setHighestPrice(
                            BigDecimalUtil.initPrice(jsonObject.getString("3"))
                    );
                }
                if (jsonObject.containsKey("4")) {
                    iFengFiveDayMinuteKLinePo.setLowestPrice(
                            BigDecimalUtil.initPrice(jsonObject.getString("4"))
                    );
                }
                if (jsonObject.containsKey("5")) {
                    iFengFiveDayMinuteKLinePo.setDt(jsonObject.getString("5"));
                }
                JSONArray recordArr = jsonObject.containsKey(RESPONSE_KEY_RECORD) ?
                        jsonObject.getJSONArray(RESPONSE_KEY_RECORD) : null;
                if (null == recordArr || recordArr.isEmpty()) {
                    continue;
                }
                List<IFengFiveDayMinuteNodeDataPo> iFengFiveDayMinuteNodeDataPoList = null;
                for (int j = 0; j < recordArr.size(); j++) {
                    JSONArray minuteArr = recordArr.getJSONArray(j);
                    if (null == minuteArr || minuteArr.isEmpty()) {
                        continue;
                    }
                    IFengFiveDayMinuteNodeDataPo iFengFiveDayMinuteNodeDataPo = null;
                    for (int k = 0; k < minuteArr.size(); k++) {
                        String str = minuteArr.getString(k);
                        str = str.replaceAll(",", "");
                        if (null == str || str.isEmpty()) {
                            continue;
                        }
                        if (null == iFengFiveDayMinuteNodeDataPo) {
                            iFengFiveDayMinuteNodeDataPo = new IFengFiveDayMinuteNodeDataPo();
                        }
                        if (0 == k) {
                            iFengFiveDayMinuteNodeDataPo.setTime(
                                    DateUtil.dateStrFormatChange(str, DateUtil.TIME_FORMAT_8, DateUtil.TIME_FORMAT_2)
                            );
                        } else if (1 == k) {
                            iFengFiveDayMinuteNodeDataPo.setPrice(BigDecimalUtil.initPrice(str));
                        } else if (2 == k) {
                            iFengFiveDayMinuteNodeDataPo.setUptickRate(BigDecimalUtil.initPrice(str));
                        } else if (3 == k) {
                            iFengFiveDayMinuteNodeDataPo.setDealMoney(BigDecimalUtil.initPrice(str));
                        } else if (4 == k) {
                            iFengFiveDayMinuteNodeDataPo.setAvgPrice(BigDecimalUtil.initPrice(str));
                        } else if (5 == k) {
                            iFengFiveDayMinuteNodeDataPo.setQrrRate(BigDecimalUtil.initRate(str));
                        } else {
                            break;
                        }
                    }
                    if (null != iFengFiveDayMinuteNodeDataPo) {
                        iFengFiveDayMinuteNodeDataPoList = null == iFengFiveDayMinuteNodeDataPoList ?
                                new ArrayList<>() : iFengFiveDayMinuteNodeDataPoList;
                        iFengFiveDayMinuteNodeDataPoList.add(iFengFiveDayMinuteNodeDataPo);
                    }
                }
                if (null == iFengFiveDayMinuteNodeDataPoList) {
                    continue;
                }
                iFengFiveDayMinuteKLinePo.setKlineData(iFengFiveDayMinuteNodeDataPoList);
                iFengFiveDayMinuteKLinePoList = null == iFengFiveDayMinuteKLinePoList ?
                        new ArrayList<>() : iFengFiveDayMinuteKLinePoList;
                iFengFiveDayMinuteKLinePoList.add(iFengFiveDayMinuteKLinePo);
            }
            return iFengFiveDayMinuteKLinePoList;
        } catch (Exception e) {
            logger.error(response, e);
        }
        return null;
    }
}
