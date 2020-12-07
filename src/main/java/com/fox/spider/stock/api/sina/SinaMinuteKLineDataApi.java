package com.fox.spider.stock.api.sina;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sina.SinaMinuteKLineDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 获取分钟粒度成交信息
 * 不支持港股
 * 返回数据包含当前交易日数据
 * scale参数大于等240，时间只有日期
 * 240:按天数据
 * 1200： 按周数据
 * 86400: 按年数据
 *
 * @author lusongsong
 * @date 2020/11/4 16:37
 */
@Component
public class SinaMinuteKLineDataApi extends SinaBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 样例链接
     * http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=sz002095&scale=60&ma=no&datalen=1023
     */
    /**
     * 接口
     */
    private static String apiUrl = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData";
    /**
     * 支持的时间粒度
     */
    private static List<Integer> scaleList = Arrays.asList(5, 15, 30, 60, 240, 1200, 1680, 86400);

    /**
     * 获取分钟粒度成交信息
     *
     * @param stockVo
     * @param scale
     * @param dataLen
     * @return
     */
    public List<SinaMinuteKLineDataPo> kLineDataList(StockVo stockVo, Integer scale, Integer dataLen) {
        List<SinaMinuteKLineDataPo> list = new ArrayList<>();
        try {
            if (!scaleList.contains(scale)) {
                return list;
            }
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(apiUrl)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            httpUtil.setParam("symbol", SinaBaseApi.sinaStockCode(stockVo));
            httpUtil.setParam("scale", Integer.toString(scale));
            httpUtil.setParam("datalen", Integer.toString(dataLen));
            httpUtil.setParam("ma", "no");
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
        }
        return list;
    }

    /**
     * 处理接口返回
     *
     * @param response
     * @return
     */
    private List<SinaMinuteKLineDataPo> handleResponse(String response) {
        List<SinaMinuteKLineDataPo> list = new ArrayList<>();
        try {
            //其中的这个data是接口传来的json数据
            JSONArray jsonArray = JSONArray.parseArray(response);
            int arrayLen = jsonArray.size();
            String day, time;
            Integer timePos;
            for (int i = 0; i < arrayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SinaMinuteKLineDataPo stockDealEntity = new SinaMinuteKLineDataPo();
                day = jsonObject.getString("day");
                timePos = day.indexOf(" ");
                time = -1 != timePos ? day.substring(timePos + 1) : "";
                day = -1 != timePos ? day.substring(0, timePos) : day;
                stockDealEntity.setDt(day);
                stockDealEntity.setTime(time);
                stockDealEntity.setOpenPrice(BigDecimalUtil.initPrice(jsonObject.getDouble("open")));
                stockDealEntity.setHighestPrice(BigDecimalUtil.initPrice(jsonObject.getDouble("high")));
                stockDealEntity.setLowestPrice(BigDecimalUtil.initPrice(jsonObject.getDouble("low")));
                stockDealEntity.setClosePrice(BigDecimalUtil.initPrice(jsonObject.getDouble("close")));
                stockDealEntity.setDealNum(jsonObject.getLong("volume"));
                list.add(stockDealEntity);
            }
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
        return list;
    }
}
