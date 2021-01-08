package com.fox.spider.stock.api.ifeng;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.ifeng.IFengRealtimeBigDealPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 凤凰网大单交易信息
 *
 * @author lusongsong
 * @date 2021/1/7 15:24
 */
@Component
public class IFengRealtimeBigDealApi extends IFengBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * http://app.finance.ifeng.com/hq/stock_bill.php?code=sz002475
     */
    private static final String API_URL = "http://app.finance.ifeng.com/hq/stock_bill.php?code=";

    /**
     * 获取交易类型
     *
     * @param text
     * @return
     */
    private int getDealType(String text) {
        switch (text) {
            case "买盘":
                return StockConst.DEAL_BUY;
            case "卖盘":
                return StockConst.DEAL_SELL;
            case "其他":
            case "中性盘":
                return StockConst.DEAL_FLAT;
            default:
                return StockConst.DEAL_UNKNOWN;
        }
    }

    /**
     * 获取大单交易数据
     *
     * @param stockVo
     * @return
     */
    public List<IFengRealtimeBigDealPo> bigDeal(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String iFengStockCode = iFengStockCode(stockVo);
            Document document = Jsoup.connect(API_URL + iFengStockCode).get();
            return handleResponse(document);
        } catch (IOException e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 处理返回数据
     *
     * @param document
     * @return
     */
    private List<IFengRealtimeBigDealPo> handleResponse(Document document) {
        List<IFengRealtimeBigDealPo> iFengRealtimeBigDealPoList = null;
        if (null != document) {
            Elements tableElements = document.body().getElementsByTag("table");
            if (null != tableElements && !tableElements.isEmpty()) {
                Element tableElement = tableElements.first();
                Elements trElements = tableElement.getElementsByTag("tr");
                if (null != trElements && !trElements.isEmpty()) {
                    for (Element trElement : trElements) {
                        if (null != trElement) {
                            Elements tdElements = trElement.children();
                            IFengRealtimeBigDealPo iFengRealtimeBigDealPo = handleRow(tdElements);
                            if (null != iFengRealtimeBigDealPo) {
                                iFengRealtimeBigDealPoList = null == iFengRealtimeBigDealPoList ?
                                        new ArrayList<>() : iFengRealtimeBigDealPoList;
                                iFengRealtimeBigDealPoList.add(iFengRealtimeBigDealPo);
                            }
                        }
                    }
                }
            }
        }
        return iFengRealtimeBigDealPoList;
    }

    /**
     * 处理单条大单交易
     *
     * @param tdElements
     * @return
     */
    private IFengRealtimeBigDealPo handleRow(Elements tdElements) {
        IFengRealtimeBigDealPo iFengRealtimeBigDealPo = null;
        if (null != tdElements && !tdElements.isEmpty()) {
            for (int i = 0; i < tdElements.size(); i++) {
                Element tdElement = tdElements.get(i);
                if (tdElement.attr("width").isEmpty()) {
                    iFengRealtimeBigDealPo = null == iFengRealtimeBigDealPo ?
                            new IFengRealtimeBigDealPo() : iFengRealtimeBigDealPo;
                    String text = tdElement.text();
                    if (0 == i) {
                        iFengRealtimeBigDealPo.setTime(text);
                    } else if (1 == i) {
                        iFengRealtimeBigDealPo.setPrice(BigDecimalUtil.initPrice(text));
                    } else if (2 == i) {
                        iFengRealtimeBigDealPo.setDealNum(BigDecimalUtil.initLong(text));
                    } else if (3 == i) {
                        iFengRealtimeBigDealPo.setDealMoney(BigDecimalUtil.initPrice(text));
                    } else if (4 == i) {
                        iFengRealtimeBigDealPo.setDealType(getDealType(text));
                    } else if (5 == i) {
                        iFengRealtimeBigDealPo.setUptickPrice(BigDecimalUtil.initPrice(text));
                    } else if (6 == i) {
                        text = text.replace("%", "");
                        iFengRealtimeBigDealPo.setUptickRate(BigDecimalUtil.initRate(text));
                    }

                }
            }
        }
        return iFengRealtimeBigDealPo;
    }
}
