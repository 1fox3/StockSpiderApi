package com.fox.spider.stock.api.nets;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.nets.NetsFQKLineDataPo;
import com.fox.spider.stock.entity.po.nets.NetsFQKLineNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 股票复权类型成交信息
 *
 * @author lusongsong
 * @date 2020/11/6 14:29
 */
@Component
public class NetsFQKLineDataApi extends NetsBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 样例链接
     * http://img1.money.126.net/data/hs/kline/day/history/2020/0603383.json
     */
    /**
     * 接口
     */
    private static String apiUrl = "http://img1.money.126.net/data/" +
            "{stockMarketPY}/{rehabilitationType}/day/history/{year}/{stockCode}.json";
    /**
     * 复权类型
     */
    private static Map<Integer, String> fqTypeMap = new HashMap<Integer, String>() {{
        put(StockConst.SFQ_BEFORE, "klinederc");//前复权
        put(StockConst.SFQ_AFTER, "kline");//后复权
    }};

    /**
     * 获取接口地址
     *
     * @param stockVo
     * @param year
     * @param fqType
     * @return
     */
    private String fqKLineUrl(StockVo stockVo, Integer year, Integer fqType) {
        String stockCode = NetsBaseApi.netsStockCode(stockVo);
        if (null == stockCode || stockCode.isEmpty()
                || null == year || null == fqType || !fqTypeMap.containsKey(fqType)
        ) {
            return "";
        }
        return apiUrl.replace("{stockMarketPY}", NetsBaseApi.netsStockMarketPY(stockVo.getStockMarket()))
                .replace("{rehabilitationType}", fqTypeMap.get(fqType))
                .replace("{year}", year.toString())
                .replace("{stockCode}", stockCode);
    }

    /**
     * 获取复权类型成交信息
     *
     * @param stockVo
     * @param startDate
     * @param endDate
     * @return
     */
    public NetsFQKLineDataPo fqKLineData(StockVo stockVo, String startDate, String endDate, Integer fqType) {
        if (null == stockVo || null == startDate || startDate.isEmpty() || null == endDate || endDate.isEmpty()) {
            return null;
        }
        fqType = fqTypeMap.containsKey(fqType) ? fqType : StockConst.SFQ_AFTER;

        String startYear = DateUtil.dateStrFormatChange(startDate, DateUtil.DATE_FORMAT_1, DateUtil.YEAR_FORMAT_1);
        String endYear = DateUtil.dateStrFormatChange(endDate, DateUtil.DATE_FORMAT_1, DateUtil.YEAR_FORMAT_1);
        Date beginDate = DateUtil.getDateFromStr(startDate);
        Date stopDate = DateUtil.getDateFromStr(endDate);

        String url = "";
        HttpResponseDto httpResponse;
        NetsFQKLineDataPo netsFQKLineDataPo = null;
        try {
            for (Integer year = Integer.valueOf(startYear); year <= Integer.valueOf(endYear); year++) {
                HttpUtil httpUtil = new HttpUtil();
                url = fqKLineUrl(stockVo, year, fqType);
                httpUtil.setUrl(url).setOriCharset("GBK");
                httpResponse = httpUtil.request();
                NetsFQKLineDataPo currentNetsFQKLineDataPo = this.handleResponse(httpResponse.getContent());
                List<NetsFQKLineNodeDataPo> list = currentNetsFQKLineDataPo.getKlineData();
                List<NetsFQKLineNodeDataPo> filterList = new ArrayList<>();
                if (null != list && list.size() > 0) {
                    for (NetsFQKLineNodeDataPo netsFQKLineNodeDataPo : list) {
                        Date currentDate = DateUtil.getDateFromStr(netsFQKLineNodeDataPo.getDt());
                        if (beginDate.compareTo(currentDate) <= 0 && currentDate.compareTo(stopDate) <= 0) {
                            filterList.add(netsFQKLineNodeDataPo);
                        }
                    }
                    currentNetsFQKLineDataPo.setKlineData(filterList);
                }
                if (null == netsFQKLineDataPo || null == netsFQKLineDataPo.getStockCode()) {
                    netsFQKLineDataPo = currentNetsFQKLineDataPo;
                } else {
                    List<NetsFQKLineNodeDataPo> allList = netsFQKLineDataPo.getKlineData();
                    if (null != currentNetsFQKLineDataPo.getKlineData()) {
                        allList.addAll(currentNetsFQKLineDataPo.getKlineData());
                    }
                    netsFQKLineDataPo.setKlineData(allList);
                }
            }
        } catch (IOException e) {
            logger.error(url);
            logger.error(e.getMessage());
        }
        return netsFQKLineDataPo;
    }

    /**
     * 解析数据返回
     *
     * @param response
     * @return
     */
    private NetsFQKLineDataPo handleResponse(String response) {
        if (null == response || response.isEmpty()) {
            return null;
        }
        try {
            NetsFQKLineDataPo netsFQKLineDataPo = new NetsFQKLineDataPo();
            JSONObject responseObj = (JSONObject) JSONObject.fromObject(response);
            if (responseObj.containsKey("symbol")) {
                netsFQKLineDataPo.setStockCode(responseObj.getString("symbol"));
            }
            if (responseObj.containsKey("name")) {
                netsFQKLineDataPo.setStockName(responseObj.getString("name").replace(" ", ""));
            }
            if (responseObj.containsKey("data")) {
                JSONArray dataArr = (JSONArray) responseObj.get("data");
                int dataLen = dataArr.size();
                List<NetsFQKLineNodeDataPo> nodeList = new ArrayList<>();
                for (int i = 0; i < dataLen; i++) {
                    JSONArray singleArr = (JSONArray) dataArr.get(i);
                    if (7 == singleArr.size()) {
                        NetsFQKLineNodeDataPo netsFQKLineNodeDataPo = new NetsFQKLineNodeDataPo();
                        netsFQKLineNodeDataPo.setDt(
                                DateUtil.dateStrFormatChange(
                                        singleArr.getString(0), DateUtil.DATE_FORMAT_2, DateUtil.DATE_FORMAT_1
                                )
                        );
                        netsFQKLineNodeDataPo.setOpenPrice(BigDecimalUtil.initPrice(singleArr.getDouble(1)));
                        netsFQKLineNodeDataPo.setClosePrice(BigDecimalUtil.initPrice(singleArr.getDouble(2)));
                        netsFQKLineNodeDataPo.setHighestPrice(BigDecimalUtil.initPrice(singleArr.getDouble(3)));
                        netsFQKLineNodeDataPo.setLowestPrice(BigDecimalUtil.initPrice(singleArr.getDouble(4)));
                        netsFQKLineNodeDataPo.setDealNum(singleArr.getLong(5));
                        netsFQKLineNodeDataPo.setUptickRate(BigDecimalUtil.initPrice(singleArr.getDouble(6)));
                        nodeList.add(netsFQKLineNodeDataPo);
                    }
                }
                if (0 < nodeList.size()) {
                    netsFQKLineDataPo.setKlineData(nodeList);
                }
            }
            return netsFQKLineDataPo;
        } catch (JSONException e) {
            logger.error(response);
            logger.error(e.getMessage());
        }
        return null;
    }
}
