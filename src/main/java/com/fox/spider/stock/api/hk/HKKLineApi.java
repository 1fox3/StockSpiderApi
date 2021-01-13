package com.fox.spider.stock.api.hk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.hk.HKKLineNodeDataPo;
import com.fox.spider.stock.entity.po.hk.HKKLinePo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import com.fox.spider.stock.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 港股K线数据接口
 *
 * @author lusongsong
 * @date 2021/1/12 19:14
 */
@Component
public class HKKLineApi extends HKBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     */
    private static final String API_URL = "https://www1.hkex.com.hk/hkexwidget/data/getchartdata2";
    /**
     * 最新交易日分钟线图
     */
    public static final int HK_KLINE_MINUTE = 1;
    /**
     * 1个月
     */
    public static final int HK_KLINE_MONTH_ONE = 2;
    /**
     * 3个月
     */
    public static final int HK_KLINE_MONTH_THREE = 3;
    /**
     * 6个月
     */
    public static final int HK_KLINE_MONTH_SIX = 4;
    /**
     * 本年至今
     */
    public static final int HK_KLINE_YEAR_CURRENT = 5;
    /**
     * 1年
     */
    public static final int HK_KLINE_YEAR_ONE = 6;
    /**
     * 2年
     */
    public static final int HK_KLINE_YEAR_TWO = 7;
    /**
     * 5年
     */
    public static final int HK_KLINE_YEAR_FIVE = 8;
    /**
     * 10年
     */
    public static final int HK_KLINE_YEAR_TEN = 9;

    /**
     * 获取线图类型参数
     *
     * @param kLineType
     * @return
     */
    private Map<String, Object> getKLintTypeParams(Integer kLineType) {
        int spanParam = 0, intParam = 0;
        switch (kLineType) {
            case HK_KLINE_MONTH_ONE:
                spanParam = 6;
                intParam = 2;
                break;
            case HK_KLINE_MONTH_THREE:
                spanParam = 6;
                intParam = 3;
                break;
            case HK_KLINE_MONTH_SIX:
                spanParam = 6;
                intParam = 4;
                break;
            case HK_KLINE_YEAR_CURRENT:
                spanParam = 6;
                intParam = 9;
                break;
            case HK_KLINE_YEAR_ONE:
                spanParam = 6;
                intParam = 5;
                break;
            case HK_KLINE_YEAR_TWO:
                spanParam = 6;
                intParam = 6;
                break;
            case HK_KLINE_YEAR_FIVE:
                spanParam = 7;
                intParam = 7;
                break;
            case HK_KLINE_YEAR_TEN:
                spanParam = 8;
                intParam = 8;
                break;
            default:
                spanParam = 0;
                intParam = 0;
        }
        Map<String, Object> params = new HashMap<>(2);
        params.put("span", spanParam);
        params.put("int", intParam);
        return params;
    }

    /**
     * 港股K线数据
     *
     * @param stockVo
     * @param kLineType
     * @param token
     * @return
     */
    public HKKLinePo kLine(StockVo stockVo, Integer kLineType, String token) {
        if (!StockVo.verify(stockVo) || StockConst.SM_HK != stockVo.getStockMarket()) {
            return null;
        }
        try {
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParam("hchart", 1)
                    .setParam("qid", "1610450620607")
                    .setParam("callback", "jQuery35107328348989801241_1610449968976")
                    .setParam("token", token)
                    .setParam("ric", stockVo.getStockCode().substring(1, 5) + ".HK")
                    .setParams(getKLintTypeParams(kLineType));
            HttpResponseDto httpResponse = httpUtil.request();
            String response = JSONUtil.objectStrTrim(httpResponse.getContent());
            return handleResponse(stockVo, response);
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
    private HKKLinePo handleResponse(StockVo stockVo, String response) {
        if (!StockVo.verify(stockVo) || StringUtils.isEmpty(response)) {
            return null;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            if (jsonObject.containsKey("data")) {
                jsonObject = jsonObject.getJSONObject("data");
                HKKLinePo hkkLinePo = new HKKLinePo();
                hkkLinePo.setStockMarket(stockVo.getStockMarket());
                hkkLinePo.setStockCode(stockVo.getStockCode());
                if (jsonObject.containsKey("datalist")) {
                    JSONArray datalist = jsonObject.getJSONArray("datalist");
                    hkkLinePo.setKlineData(handleDataList(datalist));
                }
                return hkkLinePo;
            }
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 解析K线数据
     *
     * @param jsonArray
     * @return
     */
    private List<HKKLineNodeDataPo> handleDataList(JSONArray jsonArray) {
        if (null == jsonArray || jsonArray.isEmpty()) {
            return null;
        }
        List<HKKLineNodeDataPo> hkkLineNodeDataPoList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray nodeArr = jsonArray.getJSONArray(i);
            if (nodeArr.isEmpty() || nodeArr.contains(null)) {
                continue;
            }
            HKKLineNodeDataPo hkkLineNodeDataPo = new HKKLineNodeDataPo();
            for (int j = 0; j < nodeArr.size(); j++) {
                if (0 == j) {
                    Long timestamp = nodeArr.getLong(j);
                    hkkLineNodeDataPo.setDt(
                            DateUtil.timestampFormat(timestamp, DateUtil.DATE_FORMAT_1)
                    );
                    hkkLineNodeDataPo.setTime(
                            DateUtil.timestampFormat(timestamp, DateUtil.TIME_FORMAT_2)
                    );
                } else if (1 == j) {
                    hkkLineNodeDataPo.setOpenPrice(
                            BigDecimalUtil.initPrice(nodeArr.getString(j))
                    );
                } else if (2 == j) {
                    hkkLineNodeDataPo.setHighestPrice(
                            BigDecimalUtil.initPrice(nodeArr.getString(j))
                    );
                } else if (3 == j) {
                    hkkLineNodeDataPo.setLowestPrice(
                            BigDecimalUtil.initPrice(nodeArr.getString(j))
                    );
                } else if (4 == j) {
                    hkkLineNodeDataPo.setClosePrice(
                            BigDecimalUtil.initPrice(nodeArr.getString(j))
                    );
                } else if (5 == j) {
                    hkkLineNodeDataPo.setDealNum(
                            BigDecimalUtil.initLong(nodeArr.getString(j))
                    );
                } else if (6 == j) {
                    hkkLineNodeDataPo.setDealMoney(
                            BigDecimalUtil.initPrice(nodeArr.getString(j))
                    );
                }
            }
            hkkLineNodeDataPoList.add(hkkLineNodeDataPo);
        }
        return hkkLineNodeDataPoList;
    }
}
