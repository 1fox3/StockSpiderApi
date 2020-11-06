package com.fox.spider.stock.api.nets;

import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteDealInfoPo;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 股票实时交易分钟线图数据
 *
 * @author lusongsong
 * @date 2020/11/6 15:21
 */
@Component
public class NetsRealtimeMinuteDealInfo extends NetsBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 样例链接
     * http://img1.money.126.net/data/hs/time/today/1399001.json
     */
    /**
     * 接口
     */
    private static String apiUrl = "http://img1.money.126.net/data/{stockMarketPY}/time/today/{stockCode}.json";

    /**
     * 获取实时交易分钟线图数据
     *
     * @param stockVo
     * @return
     */
    public NetsRealtimeMinuteDealInfoPo realtimeMinuteKLine(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String url = apiUrl.replace("{stockCode}", NetsBaseApi.netsStockCode(stockVo))
                    .replace("{stockMarketPY}", NetsBaseApi.netsStockMarketPY(stockVo.getStockMarket()));
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
     * 处理返回数据
     *
     * @param response
     * @return
     */
    private NetsRealtimeMinuteDealInfoPo handleResponse(String response) {
        if (null == response || response.isEmpty()) {
            return null;
        }

        try {
            NetsRealtimeMinuteDealInfoPo netsRealtimeMinuteDealInfoPo = new NetsRealtimeMinuteDealInfoPo();
            JSONObject responseObject = JSONObject.fromObject(response);
            if (responseObject.containsKey("count")) {
                netsRealtimeMinuteDealInfoPo.setNodeCount(responseObject.getInt("count"));
            }
            if (responseObject.containsKey("symbol")) {
                netsRealtimeMinuteDealInfoPo.setStockCode(responseObject.getString("symbol"));
            }
            if (responseObject.containsKey("name")) {
                netsRealtimeMinuteDealInfoPo.setStockName(responseObject.getString("name").replace(" ", ""));
            }
            if (responseObject.containsKey("yestclose")) {
                netsRealtimeMinuteDealInfoPo.setPreClosePrice(new BigDecimal(responseObject.getDouble("yestclose")));
            }
            if (responseObject.containsKey("lastVolume")) {
                netsRealtimeMinuteDealInfoPo.setDealNum(responseObject.getLong("lastVolume"));
            }
            if (responseObject.containsKey("date")) {
                netsRealtimeMinuteDealInfoPo.setDt(
                        DateUtil.dateStrFormatChange(
                                responseObject.getString("date"),
                                DateUtil.DATE_FORMAT_2,
                                DateUtil.DATE_FORMAT_1
                        )
                );
            }
            if (responseObject.containsKey("data")) {
                JSONArray dataArr = (JSONArray) responseObject.get("data");
                List<NetsRealtimeMinuteNodeDataPo> nodeList = new ArrayList<>();
                int dataLen = dataArr.size();
                for (int i = 0; i < dataLen; i++) {
                    JSONArray noteArr = (JSONArray) dataArr.get(i);
                    if (4 == noteArr.size()) {
                        NetsRealtimeMinuteNodeDataPo netsRealtimeMinuteNodeDataPo = new NetsRealtimeMinuteNodeDataPo();
                        String timeStr = noteArr.getString(0);
                        netsRealtimeMinuteNodeDataPo.setTime(timeStr.substring(0, 2) + ":" + timeStr.substring(2, 4));
                        netsRealtimeMinuteNodeDataPo.setPrice(BigDecimalUtil.initPrice(noteArr.getDouble(1)));
                        netsRealtimeMinuteNodeDataPo.setAvgPrice(BigDecimalUtil.initPrice(noteArr.getDouble(2)));
                        netsRealtimeMinuteNodeDataPo.setDealNum(noteArr.getLong(3));
                        nodeList.add(netsRealtimeMinuteNodeDataPo);
                    }
                }
                if (nodeList.size() > 0) {
                    netsRealtimeMinuteDealInfoPo.setKlineData(nodeList);
                }
            }
            return netsRealtimeMinuteDealInfoPo;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
