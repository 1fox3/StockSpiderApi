package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentMinuteScopeKLinePo;
import com.fox.spider.stock.entity.po.tencent.TencentMinuteScopeNodeDataPo;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 腾讯股票分钟线图交易数据
 * 只支持A股
 *
 * @author lusongsong
 * @date 2020/12/28 16:36
 */
@Component
public class TencentMinuteScopeKLineApi extends TencentKLineBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://ifzq.gtimg.cn/appstock/app/kline/mkline?param=sz000002,m5,,320&_var=m5_today
     */
    private static final String API_URL = "https://ifzq.gtimg.cn/appstock/app/kline/mkline";
    /**
     * 范围列表
     */
    private static List<Integer> SCOPE_LIST = Arrays.asList(5, 30, 60);
    /**
     * 能拿到的最大数据长度
     */
    private static int MAX_LEN = 800;

    /**
     * 获取股票不同分钟粒度线图
     *
     * @param stockVo
     * @param scope
     * @param len
     * @return
     */
    public TencentMinuteScopeKLinePo minuteScopeKLine(StockVo stockVo, Integer scope, Integer len) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()
                || null == scope || scope < 0 || !SCOPE_LIST.contains(scope) || null == len || len < 0) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            len = MAX_LEN < len ? MAX_LEN : len;
            Map<String, Object> params = new HashMap<>(2);
            params.put("param", StringUtils.join(Arrays.asList(
                    tencnetStockCode,
                    "m" + scope,
                    "",
                    len.toString()
            ), ","));
            params.put("_var", "m" + scope + "_today");
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            TencentMinuteScopeKLinePo tencentMinuteScopeKLinePo = handleResponse(
                    stockVo, scope, httpResponse.getContent()
            );
            if (null != tencentMinuteScopeKLinePo) {
                tencentMinuteScopeKLinePo.setStockMarket(stockVo.getStockMarket());
                tencentMinuteScopeKLinePo.setStockCode(stockVo.getStockCode());
            }
            return tencentMinuteScopeKLinePo;
        } catch (IOException e) {
            logger.error(scope + ":" + len);
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
    private TencentMinuteScopeKLinePo handleResponse(StockVo stockVo, Integer scope, String response) {
        if (null == stockVo || null == response || response.isEmpty()) {
            return null;
        }
        try {
            String stockCode = TencentBaseApi.tencentStockCode(stockVo);
            JSONObject jsonObject = getResponseJSONObject(stockVo, response);
            TencentMinuteScopeKLinePo tencentMinuteScopeKLinePo = new TencentMinuteScopeKLinePo();
            if (jsonObject.containsKey(RESPONSE_KEY_PRE_CLOSE_PRICE)) {
                tencentMinuteScopeKLinePo.setPreClosePrice(
                        BigDecimalUtil.initPrice(jsonObject.getString(RESPONSE_KEY_PRE_CLOSE_PRICE))
                );
            }
            JSONObject dealInfoObject = jsonObject.containsKey(RESPONSE_KEY_QT) ?
                    jsonObject.getJSONObject(RESPONSE_KEY_QT) : null;
            if (null != dealInfoObject && dealInfoObject.containsKey(stockCode)) {
                JSONArray dealInfoArr = dealInfoObject.getJSONArray(stockCode);
                TencentRealtimeDealInfoPo tencentRealtimeDealInfoPo =
                        TencentRealtimeDealInfoApi.getDealInfo(stockVo, dealInfoArr.toArray(new String[]{}));
                if (null != tencentRealtimeDealInfoPo) {
                    tencentMinuteScopeKLinePo.setRealtimeDealInfo(tencentRealtimeDealInfoPo);
                    tencentMinuteScopeKLinePo.setStockName(
                            tencentRealtimeDealInfoPo.getStockName()
                    );
                }
            }
            String minuteScopeDataKey = "m" + scope;
            JSONArray minuteScopeArr = jsonObject.containsKey(minuteScopeDataKey) ?
                    jsonObject.getJSONArray(minuteScopeDataKey) : null;
            if (null != minuteScopeArr && !minuteScopeArr.isEmpty()) {
                List<TencentMinuteScopeNodeDataPo> tencentMinuteScopeNodeDataPoList = new ArrayList<>();
                for (int dateIndex = 0; dateIndex < minuteScopeArr.size(); dateIndex++) {
                    JSONArray oneScopeArr = minuteScopeArr.getJSONArray(dateIndex);
                    if (null == oneScopeArr || oneScopeArr.isEmpty()) {
                        continue;
                    }
                    TencentMinuteScopeNodeDataPo tencentMinuteScopeNodeDataPo = new TencentMinuteScopeNodeDataPo();
                    for (int i = 0; i < oneScopeArr.size(); i++) {
                        if (0 == i) {
                            tencentMinuteScopeNodeDataPo.setDt(
                                    DateUtil.dateStrFormatChange(
                                            oneScopeArr.getString(i),
                                            DateUtil.TIME_FORMAT_7,
                                            DateUtil.DATE_FORMAT_1
                                    )
                            );
                            tencentMinuteScopeNodeDataPo.setTime(
                                    DateUtil.dateStrFormatChange(
                                            oneScopeArr.getString(i),
                                            DateUtil.TIME_FORMAT_7,
                                            DateUtil.TIME_FORMAT_6
                                    )
                            );
                        }
                        if (1 == i) {
                            tencentMinuteScopeNodeDataPo.setOpenPrice(
                                    BigDecimalUtil.initPrice(oneScopeArr.getString(i))
                            );
                        }
                        if (2 == i) {
                            tencentMinuteScopeNodeDataPo.setClosePrice(
                                    BigDecimalUtil.initPrice(oneScopeArr.getString(i))
                            );
                        }
                        if (3 == i) {
                            tencentMinuteScopeNodeDataPo.setHighestPrice(
                                    BigDecimalUtil.initPrice(oneScopeArr.getString(i))
                            );
                        }
                        if (4 == i) {
                            tencentMinuteScopeNodeDataPo.setLowestPrice(
                                    BigDecimalUtil.initPrice(oneScopeArr.getString(i))
                            );
                        }
                        if (5 == i) {
                            tencentMinuteScopeNodeDataPo.setDealNum(
                                    handleDealNum(stockVo, oneScopeArr.getString(i))
                            );
                        }
                    }
                    tencentMinuteScopeNodeDataPoList.add(tencentMinuteScopeNodeDataPo);
                }
                tencentMinuteScopeKLinePo.setNodeCount(tencentMinuteScopeNodeDataPoList.size());
                tencentMinuteScopeKLinePo.setKlineData(tencentMinuteScopeNodeDataPoList);
            }
            return tencentMinuteScopeKLinePo;
        } catch (Exception e) {
            logger.error(response, e);
        }
        return null;
    }
}
