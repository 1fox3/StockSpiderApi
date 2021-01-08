package com.fox.spider.stock.api.ifeng;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.ifeng.IFengRealtimePriceDealNumPo;
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
 * 凤凰网价格成交量信息
 *
 * @author lusongsong
 * @date 2021/1/7 18:28
 */
@Component
public class IFengRealtimePriceDealNumApi extends IFengBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * http://app.finance.ifeng.com/data/stock/stock_stat3.php?code=sz002475
     */
    private static final String API_URL = "http://app.finance.ifeng.com/data/stock/stock_stat3.php?code=";

    /**
     * 获取大单交易数据
     *
     * @param stockVo
     * @return
     */
    public List<IFengRealtimePriceDealNumPo> priceDealNum(StockVo stockVo) {
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
    private List<IFengRealtimePriceDealNumPo> handleResponse(Document document) {
        List<IFengRealtimePriceDealNumPo> iFengRealtimePriceDealNumPoList = null;
        if (null != document) {
            Elements tableElements = document.body().getElementsByTag("table");
            if (null != tableElements && !tableElements.isEmpty()) {
                Element tableElement = tableElements.first();
                Elements trElements = tableElement.getElementsByTag("tr");
                if (null != trElements && !trElements.isEmpty()) {
                    for (Element trElement : trElements) {
                        if (null != trElement) {
                            Elements tdElements = trElement.children();
                            IFengRealtimePriceDealNumPo iFengRealtimePriceDealNumPo = handleRow(tdElements);
                            if (null != iFengRealtimePriceDealNumPo) {
                                iFengRealtimePriceDealNumPoList = null == iFengRealtimePriceDealNumPoList ?
                                        new ArrayList<>() : iFengRealtimePriceDealNumPoList;
                                iFengRealtimePriceDealNumPoList.add(iFengRealtimePriceDealNumPo);
                            }
                        }
                    }
                }
            }
        }
        return iFengRealtimePriceDealNumPoList;
    }

    /**
     * 处理单条大单交易
     *
     * @param tdElements
     * @return
     */
    private IFengRealtimePriceDealNumPo handleRow(Elements tdElements) {
        IFengRealtimePriceDealNumPo iFengRealtimePriceDealNumPo = null;
        if (null != tdElements && !tdElements.isEmpty()) {
            for (int i = 0; i < tdElements.size(); i++) {
                Element tdElement = tdElements.get(i);
                if (tdElement.getElementsByTag("th").isEmpty()) {
                    iFengRealtimePriceDealNumPo = null == iFengRealtimePriceDealNumPo ?
                            new IFengRealtimePriceDealNumPo() : iFengRealtimePriceDealNumPo;
                    String text = tdElement.text();
                    if (0 == i) {
                        iFengRealtimePriceDealNumPo.setPrice(BigDecimalUtil.initPrice(text));
                    } else if (1 == i) {
                        iFengRealtimePriceDealNumPo.setDealNum(BigDecimalUtil.initLong(text));
                    } else if (2 == i) {
                        text = text.replace("%", "");
                        iFengRealtimePriceDealNumPo.setRatio(BigDecimalUtil.initRate(text));
                    }

                }
            }
        }
        return iFengRealtimePriceDealNumPo;
    }
}
