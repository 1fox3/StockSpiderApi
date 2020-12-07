package com.fox.spider.stock.api.nets;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 股票不同日期类型复权收盘价
 *
 * @author lusongsong
 * @date 2020/11/6 15:34
 */
@Component
public class NetsFQTotalClosePriceApi extends NetsBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 样例链接
     * http://img1.money.126.net/data/hs/kline/day/times/1399001.json
     */
    /**
     *
     */
    private static String apiUrl = "http://img1.money.126.net/data/" +
            "{stockMarketPY}/{rehabilitationType}/{dateType}/times/{stockCode}.json";
    /**
     * 复权类型
     */
    private static Map<Integer, String> fqTypeMap = new HashMap<Integer, String>() {{
        put(StockConst.SFQ_BEFORE, "klinederc");//前复权
        put(StockConst.SFQ_AFTER, "kline");//后复权
    }};
    /**
     * 日期类型
     */
    private static Map<Integer, String> dateTypeMap = new HashMap<Integer, String>() {{
        put(StockConst.DT_DAY, "day");//天
        put(StockConst.DT_WEEK, "week");//周
        put(StockConst.DT_MONTH, "month");//月
    }};

    /**
     * 获取不同日期类型复权收盘价
     *
     * @param stockVo
     * @param dateType
     * @param fqType
     * @return
     */
    public Map<String, BigDecimal> fqClosePrice(StockVo stockVo, Integer dateType, Integer fqType) {
        if (null == stockVo || null == stockVo.getStockCode() || null == stockVo.getStockMarket()
                || null == dateType || !dateTypeMap.containsKey(dateType)
                || null == fqType || !fqTypeMap.containsKey(fqType)) {
            return null;
        }
        try {
            String url = apiUrl.replace("{stockMarketPY}", NetsBaseApi.netsStockMarketPY(stockVo.getStockMarket()))
                    .replace("{rehabilitationType}", fqTypeMap.get(fqType))
                    .replace("{dateType}", dateTypeMap.get(dateType))
                    .replace("{stockCode}", NetsBaseApi.netsStockCode(stockVo));
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(url).setOriCharset("GBK");
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 解析数据返回
     *
     * @param response
     * @return
     */
    private Map<String, BigDecimal> handleResponse(String response) {
        try {
            JSONObject responseObj = JSONObject.parseObject(response);
            JSONArray closePriceArr = (JSONArray) responseObj.get("closes");
            JSONArray dateArr = (JSONArray) responseObj.get("times");
            if (null == closePriceArr || null == dateArr || closePriceArr.isEmpty() || dateArr.isEmpty()) {
                return null;
            }
            int dateLen = dateArr.size();
            int closePriceLen = closePriceArr.size();
            Map<String, BigDecimal> dataMap = new TreeMap<>();
            if (dateLen == closePriceLen) {
                for (int i = 0; i < dateLen; i++) {
                    dataMap.put(
                            DateUtil.dateStrFormatChange(
                                    dateArr.getString(i),
                                    DateUtil.DATE_FORMAT_2,
                                    DateUtil.DATE_FORMAT_1
                            ),
                            BigDecimalUtil.initPrice(closePriceArr.getDouble(i))
                    );
                }
            }
            return dataMap;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
