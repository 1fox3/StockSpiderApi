package com.fox.spider.stock.api.ifeng;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.ifeng.IFengKLinePo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 凤凰网天周月线图数据
 *
 * @author lusongsong
 * @date 2021/1/6 16:40
 */
@Component
public class IFengKLineApi extends IFengBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://api.finance.ifeng.com/akmonthly/?code=sz300033&type=last
     */
    private static final String API_URL = "https://api.finance.ifeng.com/";
    /**
     * 支持日期类型范围类别
     */
    private static final List<Integer> DT_TYPE_LIST = Arrays.asList(
            StockConst.DT_DAY,
            StockConst.DT_WEEK,
            StockConst.DT_MONTH
    );

    /**
     * 获取接口日志
     *
     * @param dtType
     * @return
     */
    private String getApiUrl(Integer dtType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(API_URL);
        switch (dtType) {
            case StockConst.DT_WEEK:
                stringBuilder.append("akweekly/");
                break;
            case StockConst.DT_MONTH:
                stringBuilder.append("akmonthly/");
                break;
            default:
                stringBuilder.append("akdaily/");
                break;
        }
        return stringBuilder.toString();
    }

    /**
     * 获取股票不同分钟粒度线图
     *
     * @param stockVo
     * @return
     */
    public List<IFengKLinePo> kLine(StockVo stockVo, Integer dtType) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()
                || null == dtType || !DT_TYPE_LIST.contains(dtType)) {
            return null;
        }
        try {
            String iFengStockCode = iFengStockCode(stockVo);
            Map<String, Object> params = new HashMap<>(2);
            params.put("code", iFengStockCode);
            params.put("type", "last");
            HttpUtil httpUtil = new HttpUtil().setUrl(getApiUrl(dtType))
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            List<IFengKLinePo> iFengKLinePoList = this.handleResponse(
                    stockVo, httpResponse.getContent()
            );
            return iFengKLinePoList;
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
    private List<IFengKLinePo> handleResponse(StockVo stockVo, String response) {
        if (null == stockVo || null == response || response.isEmpty()) {
            return null;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            JSONArray recordArr = jsonObject.containsKey(RESPONSE_KEY_RECORD) ?
                    jsonObject.getJSONArray(RESPONSE_KEY_RECORD) : null;
            if (null == recordArr || recordArr.isEmpty()) {
                return null;
            }
            List<IFengKLinePo> iFengKLinePoList = null;
            for (int i = 0; i < recordArr.size(); i++) {
                JSONArray minuteArr = recordArr.getJSONArray(i);
                if (null == minuteArr || minuteArr.isEmpty()) {
                    continue;
                }
                IFengKLinePo iFengKLinePo = null;
                for (int j = 0; j < minuteArr.size(); j++) {
                    String str = minuteArr.getString(j);
                    if (null == str || str.isEmpty()) {
                        continue;
                    }
                    if (null == iFengKLinePo) {
                        iFengKLinePo = new IFengKLinePo();
                    }
                    if (0 == j) {
                        iFengKLinePo.setDt(str);
                    } else if (1 == j) {
                        iFengKLinePo.setOpenPrice(BigDecimalUtil.initPrice(str));
                    } else if (2 == j) {
                        iFengKLinePo.setHighestPrice(BigDecimalUtil.initPrice(str));
                    } else if (3 == j) {
                        iFengKLinePo.setClosePrice(BigDecimalUtil.initPrice(str));
                    } else if (4 == j) {
                        iFengKLinePo.setLowestPrice(BigDecimalUtil.initPrice(str));
                    } else if (5 == j) {
                        iFengKLinePo.setDealMoney(BigDecimalUtil.initPrice(str));
                    } else {
                        break;
                    }
                }
                if (null != iFengKLinePo) {
                    iFengKLinePoList = null == iFengKLinePoList ?
                            new ArrayList<>() : iFengKLinePoList;
                    iFengKLinePoList.add(iFengKLinePo);
                }
            }
            return iFengKLinePoList;
        } catch (Exception e) {
            logger.error(response, e);
        }
        return null;
    }
}
