package com.fox.spider.stock.api.sina;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 股票不同复权类型价格线
 * 不支持港股
 *
 * @author lusongsong
 * @date 2020/11/5 15:17
 */
@Component
public class SinaFQPriceLineApi extends SinaBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 样例链接
     * http://finance.sina.com.cn/realstock/company/sh603383/qianfuquan.js
     */
    /**
     * 接口
     */
    private static String apiUrl = "http://finance.sina.com.cn/realstock/company/{stockCode}/{rehabilitationType}.js";
    /**
     * 复权类型
     */
    private static Map<Integer, String> fqTypeMap = new HashMap<Integer, String>() {{
        put(StockConst.SFQ_BEFORE, "qianfuquan");//前复权
        put(StockConst.SFQ_AFTER, "houfuquan");//后复权
    }};

    /**
     * 获取复权价格信息
     *
     * @param stockVo
     * @param fqType
     * @return
     */
    public Map<String, BigDecimal> fqPriceLine(StockVo stockVo, Integer fqType) {
        Map<String, BigDecimal> map = new HashMap<>(0);
        try {
            if (!fqTypeMap.containsKey(fqType)) {
                return map;
            }
            String url = apiUrl.replace("{stockCode}", SinaBaseApi.sinaStockCode(stockVo))
                    .replace("{rehabilitationType}", fqTypeMap.get(fqType));
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(url)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return map;
    }

    /**
     * 处理返回信息
     *
     * @param response
     * @return
     */
    private Map<String, BigDecimal> handleResponse(String response) {
        //去掉返回数据中的注释信息
        response = this.clearAnnotation(response);
        //给json字符串的key加双引号
        response = this.handleJsonStr(response);
        try {
            JSONArray jsonArray = JSONArray.parseArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            JSONObject dataObject = (JSONObject) jsonObject.get("data");
            Set<String> dataKeySet = dataObject.keySet();
            Map<String, BigDecimal> map = new TreeMap<>();
            for (String key : dataKeySet) {
                BigDecimal value = BigDecimalUtil.initPrice(dataObject.getString(key));
                if (key.startsWith("_")) {
                    key = key.substring(1);
                }
                key = key.replace("_", "-");
                map.put(key, value);
            }
            return map;
        } catch (Exception e) {
            logger.error(response);
            logger.error(e.getMessage());
        }
        return new HashMap<>(0);
    }

    /**
     * 去掉返回数据中的注释信息
     *
     * @param response
     * @return
     */
    private String clearAnnotation(String response) {
        int annotationIndex = response.lastIndexOf("/*");
        return -1 == annotationIndex ? response : response.substring(0, annotationIndex);
    }
}
