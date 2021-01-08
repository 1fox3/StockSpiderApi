package com.fox.spider.stock.api.ifeng;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.ifeng.IFengFiveDayMinuteScopeKLinePo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 凤凰网5日分钟线图数据
 *
 * @author lusongsong
 * @date 2021/1/6 16:40
 */
@Component
public class IFengFiveDayMinuteScopeKLineApi extends IFengBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://api.finance.ifeng.com/akmin?scode=sz002475&type=5
     */
    private static final String API_URL = "https://api.finance.ifeng.com/akmin";
    /**
     * 支持分钟粒度范围类别
     */
    private static final List<Integer> MINUTE_SCOPE_LIST = Arrays.asList(5, 15, 130, 60);

    /**
     * 获取股票不同分钟粒度线图
     *
     * @param stockVo
     * @return
     */
    public List<IFengFiveDayMinuteScopeKLinePo> minuteScopeKLine(StockVo stockVo, Integer scope) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()
                || null == scope || !MINUTE_SCOPE_LIST.contains(scope)) {
            return null;
        }
        try {
            String iFengStockCode = iFengStockCode(stockVo);
            Map<String, String> params = new HashMap<>(2);
            params.put("scode", iFengStockCode);
            params.put("type", scope.toString());
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            List<IFengFiveDayMinuteScopeKLinePo> iFengFiveDayMinuteScopeKLinePoList = this.handleResponse(
                    stockVo, httpResponse.getContent()
            );
            return iFengFiveDayMinuteScopeKLinePoList;
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
    private List<IFengFiveDayMinuteScopeKLinePo> handleResponse(StockVo stockVo, String response) {
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
            List<IFengFiveDayMinuteScopeKLinePo> iFengFiveDayMinuteScopeKLinePoList = null;
            for (int i = 0; i < recordArr.size(); i++) {
                JSONArray minuteArr = recordArr.getJSONArray(i);
                if (null == minuteArr || minuteArr.isEmpty()) {
                    continue;
                }
                IFengFiveDayMinuteScopeKLinePo iFengFiveDayMinuteScopeKLinePo = null;
                for (int j = 0; j < minuteArr.size(); j++) {
                    String str = minuteArr.getString(j);
                    if (null == str || str.isEmpty()) {
                        continue;
                    }
                    if (null == iFengFiveDayMinuteScopeKLinePo) {
                        iFengFiveDayMinuteScopeKLinePo = new IFengFiveDayMinuteScopeKLinePo();
                    }
                    if (0 == j) {
                        iFengFiveDayMinuteScopeKLinePo.setDt(
                                DateUtil.dateStrFormatChange(str, DateUtil.TIME_FORMAT_1, DateUtil.DATE_FORMAT_1)
                        );
                        iFengFiveDayMinuteScopeKLinePo.setTime(
                                DateUtil.dateStrFormatChange(str, DateUtil.TIME_FORMAT_1, DateUtil.TIME_FORMAT_2)
                        );
                    } else if (1 == j) {
                        iFengFiveDayMinuteScopeKLinePo.setOpenPrice(BigDecimalUtil.initPrice(str));
                    } else if (2 == j) {
                        iFengFiveDayMinuteScopeKLinePo.setHighestPrice(BigDecimalUtil.initPrice(str));
                    } else if (3 == j) {
                        iFengFiveDayMinuteScopeKLinePo.setClosePrice(BigDecimalUtil.initPrice(str));
                    } else if (4 == j) {
                        iFengFiveDayMinuteScopeKLinePo.setLowestPrice(BigDecimalUtil.initPrice(str));
                    } else if (5 == j) {
                        iFengFiveDayMinuteScopeKLinePo.setDealMoney(BigDecimalUtil.initPrice(str));
                    } else {
                        break;
                    }
                }
                if (null != iFengFiveDayMinuteScopeKLinePo) {
                    iFengFiveDayMinuteScopeKLinePoList = null == iFengFiveDayMinuteScopeKLinePoList ?
                            new ArrayList<>() : iFengFiveDayMinuteScopeKLinePoList;
                    iFengFiveDayMinuteScopeKLinePoList.add(iFengFiveDayMinuteScopeKLinePo);
                }
            }
            return iFengFiveDayMinuteScopeKLinePoList;
        } catch (Exception e) {
            logger.error(response, e);
        }
        return null;
    }
}
