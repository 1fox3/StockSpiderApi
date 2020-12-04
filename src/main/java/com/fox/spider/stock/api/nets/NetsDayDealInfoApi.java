package com.fox.spider.stock.api.nets;

import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.nets.NetsDayDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 股票按天成交信息
 *
 * @author lusongsong
 * @date 2020/11/6 13:47
 */
@Component
public class NetsDayDealInfoApi extends NetsBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 无效的收盘价，表示停牌，无交易
     */
    private static final String ILLEGAL_CLOSE_PRICE = "0.0";
    /**
     * 样例链接
     * http://quotes.money.163.com/service/chddata.html?code=0603383&start=20200101&end=20200501&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP
     */
    /**
     * 接口
     */
    private static String apiUrl = "http://quotes.money.163.com/service/chddata.html";
    /**
     * 字段列表
     * TCLOSE:收盘价
     * HIGH:最高价
     * LOW:最低价
     * TOPEN:开盘价
     * LCLOSE:前收盘价
     * CHG:涨跌额
     * PCHG:涨跌幅
     * TURNOVER:换手率
     * VOTURNOVER:成交量
     * VATURNOVER:成交金额
     * TCAP:总市值
     * MCAP:流通市值
     */
    private static String fieldList = "TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    /**
     * 构建csv文件地址
     *
     * @param stockVo
     * @param startDate
     * @param endDate
     * @return
     */
    private String csvUrl(StockVo stockVo, String startDate, String endDate) {
        String stockCode = NetsBaseApi.netsStockCode(stockVo);
        if (null == stockCode || stockCode.isEmpty()
                || null == startDate || startDate.isEmpty()
                || null == endDate || endDate.isEmpty()) {
            return "";
        }
        startDate = DateUtil.dateStrFormatChange(
                startDate, DateUtil.DATE_FORMAT_1, DateUtil.DATE_FORMAT_2
        );
        endDate = DateUtil.dateStrFormatChange(
                endDate, DateUtil.DATE_FORMAT_1, DateUtil.DATE_FORMAT_2
        );
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(apiUrl);
        stringBuffer.append("?fields=");
        stringBuffer.append(fieldList);
        stringBuffer.append("&code=");
        stringBuffer.append(stockCode);
        stringBuffer.append("&start=");
        stringBuffer.append(startDate);
        stringBuffer.append("&end=");
        stringBuffer.append(endDate);
        return stringBuffer.toString();
    }

    /**
     * 解析单天交易信息
     *
     * @param dealStr
     * @return
     */
    private NetsDayDealInfoPo parseDealInfo(String dealStr) {
        if (null == dealStr || dealStr.isEmpty()) {
            return null;
        }
        String[] dayDealStringArr = dealStr.split(",");
        NetsDayDealInfoPo netsDayDealInfoPo = new NetsDayDealInfoPo();
        for (int j = 0; j < dayDealStringArr.length; j++) {
            String subStr = dayDealStringArr[j];
            if (null == subStr || subStr.length() == 0) {
                continue;
            }
            subStr = "None".equals(subStr) ? "0" : subStr;
            switch (j) {
                case 0:
                    netsDayDealInfoPo.setDt(subStr);
                    break;
                case 1:
                    netsDayDealInfoPo.setStockCode(subStr.replace("'", ""));
                    break;
                case 2:
                    netsDayDealInfoPo.setStockName(subStr.replace(" ", ""));
                    break;
                case 3:
                    if (ILLEGAL_CLOSE_PRICE.equals(subStr)) {
                        return null;
                    }
                    netsDayDealInfoPo.setClosePrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 4:
                    netsDayDealInfoPo.setHighestPrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 5:
                    netsDayDealInfoPo.setLowestPrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 6:
                    netsDayDealInfoPo.setOpenPrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 7:
                    netsDayDealInfoPo.setPreClosePrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 8:
                    netsDayDealInfoPo.setUptickPrice(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 9:
                    netsDayDealInfoPo.setUptickRate(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 10:
                    netsDayDealInfoPo.setTurnoverRate(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 11:
                    netsDayDealInfoPo.setDealNum(Long.valueOf(subStr));
                    break;
                case 12:
                    netsDayDealInfoPo.setDealMoney(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 13:
                    netsDayDealInfoPo.setTotalValue(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
                case 14:
                    netsDayDealInfoPo.setCircValue(BigDecimal.valueOf(Double.valueOf(subStr)));
                    break;
            }
        }
        return netsDayDealInfoPo;
    }

    /**
     * 获取按天成交信息
     *
     * @param stockVo
     * @param startDate
     * @param endDate
     * @return
     */
    public List<NetsDayDealInfoPo> dayDealInfo(StockVo stockVo, String startDate, String endDate) {
        List<NetsDayDealInfoPo> netsDayDealInfoPoList = new ArrayList<>();
        try {

            String csvFileUrl = csvUrl(stockVo, startDate, endDate);
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(csvFileUrl).setOriCharset("GBK");
            HttpResponseDto httpResponse = httpUtil.request();
            String dealString = httpResponse.getContent();
            if (null != dealString && !dealString.isEmpty()) {
                String[] dealStringArr = dealString.split("\n");
                for (int i = 1; i < dealStringArr.length; i++) {
                    NetsDayDealInfoPo netsDayDealInfoPo = parseDealInfo(dealStringArr[i]);
                    if (null != netsDayDealInfoPo) {
                        netsDayDealInfoPoList.add(netsDayDealInfoPo);
                    }
                }
            }
            Collections.reverse(netsDayDealInfoPoList);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return netsDayDealInfoPoList;
    }
}
