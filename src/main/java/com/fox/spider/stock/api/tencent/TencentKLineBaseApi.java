package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeMinuteNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯股票线图基类
 *
 * @author lusongsong
 * @date 2020/12/28 15:43
 */
public class TencentKLineBaseApi extends TencentBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 分钟交易信息分割符
     */
    protected static String MIN_DEAL_INFO_SPLIT_STR = " ";
    /**
     * 返回数据key
     */
    protected static String RESPONSE_KEY_DATA = "data";
    /**
     * 返回数据交易信息key
     */
    protected static String RESPONSE_KEY_QT = "qt";
    /**
     * 返回数据日期key
     */
    protected static String RESPONSE_KEY_DATE = "date";
    /**
     * 返回数据上个交易日收盘价
     */
    protected static String RESPONSE_KEY_PRE_CLOSE_PRICE = "prec";

    /**
     * 解析股票返回数据，获取信息JSONObject
     *
     * @param stockVo
     * @param response
     * @return
     */
    protected JSONObject getResponseJSONObject(StockVo stockVo, String response) {
        int infoStartIndex = response.indexOf("{");
        if (-1 == infoStartIndex) {
            return null;
        }
        String stockCode = TencentBaseApi.tencentStockCode(stockVo);
        try {
            response = response.substring(infoStartIndex);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (null == jsonObject || !jsonObject.containsKey(RESPONSE_KEY_DATA)) {
                return null;
            }
            jsonObject = jsonObject.getJSONObject(RESPONSE_KEY_DATA);
            if (null == jsonObject || !jsonObject.containsKey(stockCode)) {
                return null;
            }
            jsonObject = jsonObject.getJSONObject(stockCode);
            return jsonObject;
        } catch (JSONException e) {
            logger.error(stockVo.toString());
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 处理天分钟数据
     *
     * @param stockVo
     * @param minKLineArr
     * @return
     */
    protected List<TencentRealtimeMinuteNodeDataPo> handleDayMinArr(StockVo stockVo, JSONArray minKLineArr) {
        if (null != minKLineArr && !minKLineArr.isEmpty()) {
            List<TencentRealtimeMinuteNodeDataPo> tencentRealtimeMinuteNodeDataPoList = new ArrayList<>();
            Long totalDealNum = 0L;
            Long currentTotalDealNum = 0L;
            for (int i = 0; i < minKLineArr.size(); i++) {
                String minDealInfoStr = minKLineArr.getString(i);
                if (null == minDealInfoStr || minDealInfoStr.isEmpty()
                        || minDealInfoStr.contains(MIN_DEAL_INFO_SPLIT_STR)) {
                    String[] minDealInfoArr = minDealInfoStr.split(MIN_DEAL_INFO_SPLIT_STR);
                    if (3 != minDealInfoArr.length) {
                        continue;
                    }
                    TencentRealtimeMinuteNodeDataPo tencentRealtimeMinuteNodeDataPo =
                            new TencentRealtimeMinuteNodeDataPo();
                    tencentRealtimeMinuteNodeDataPo.setTime(
                            DateUtil.dateStrFormatChange(
                                    minDealInfoArr[0],
                                    DateUtil.TIME_FORMAT_5,
                                    DateUtil.TIME_FORMAT_6
                            )
                    );
                    tencentRealtimeMinuteNodeDataPo.setPrice(BigDecimalUtil.initPrice(minDealInfoArr[1]));
                    currentTotalDealNum = new BigDecimal(minDealInfoArr[2]).longValue();
                    tencentRealtimeMinuteNodeDataPo.setDealNum(
                            handleDealNum(stockVo, String.valueOf(currentTotalDealNum - totalDealNum))
                    );
                    totalDealNum = currentTotalDealNum;
                    tencentRealtimeMinuteNodeDataPoList.add(tencentRealtimeMinuteNodeDataPo);
                }
            }
            return tencentRealtimeMinuteNodeDataPoList;
        }
        return null;
    }
}
