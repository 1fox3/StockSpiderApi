package com.fox.spider.stock.api.ifeng;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.ifeng.IFengRealtimeDealDetailDataPo;
import com.fox.spider.stock.entity.po.ifeng.IFengRealtimeDealDetailPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
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
 * 凤凰网实时交易详情
 *
 * @author lusongsong
 * @date 2021/1/7 17:22
 */
@Component
public class IFengRealtimeDealDetailApi extends IFengBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * http://app.finance.ifeng.com/data/stock/stock_item3.php?code=sz002475
     */
    private static final String API_URL = "http://app.finance.ifeng.com/data/stock/stock_item3.php?code=";

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
    public IFengRealtimeDealDetailPo dealDetail(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String iFengStockCode = iFengStockCode(stockVo);
            Document document = Jsoup.connect(API_URL + iFengStockCode).get();
            Elements pageElements = document.select("a[title=尾页]");
            Element pageElement = pageElements.first();
            List<IFengRealtimeDealDetailDataPo> iFengRealtimeDealDetailDataPoList = handleResponse(document);
            if (null != pageElement && null != iFengRealtimeDealDetailDataPoList) {
                IFengRealtimeDealDetailPo iFengRealtimeDealDetailPo = new IFengRealtimeDealDetailPo();
                iFengRealtimeDealDetailPo.setTotalPageNum(Integer.valueOf(pageElement.text()));
                iFengRealtimeDealDetailPo.setDetailInfoList(iFengRealtimeDealDetailDataPoList);
                return iFengRealtimeDealDetailPo;
            }
        } catch (NumberFormatException e) {
            logger.error(stockVo.toString(), e);
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
    private List<IFengRealtimeDealDetailDataPo> handleResponse(Document document) {
        List<IFengRealtimeDealDetailDataPo> iFengRealtimeDealDetailDataPoList = null;
        if (null != document) {
            Elements tableElements = document.body().getElementsByTag("table");
            if (null != tableElements && !tableElements.isEmpty()) {
                Element tableElement = tableElements.first();
                Elements trElements = tableElement.getElementsByTag("tr");
                if (null != trElements && !trElements.isEmpty()) {
                    for (Element trElement : trElements) {
                        if (null != trElement) {
                            Elements tdElements = trElement.children();
                            IFengRealtimeDealDetailDataPo iFengRealtimeDealDetailDataPo = handleRow(tdElements);
                            if (null != iFengRealtimeDealDetailDataPo) {
                                iFengRealtimeDealDetailDataPoList = null == iFengRealtimeDealDetailDataPoList ?
                                        new ArrayList<>() : iFengRealtimeDealDetailDataPoList;
                                iFengRealtimeDealDetailDataPoList.add(iFengRealtimeDealDetailDataPo);
                            }
                        }
                    }
                }
            }
        }
        return iFengRealtimeDealDetailDataPoList;
    }

    /**
     * 处理单条大单交易
     *
     * @param tdElements
     * @return
     */
    private IFengRealtimeDealDetailDataPo handleRow(Elements tdElements) {
        IFengRealtimeDealDetailDataPo iFengRealtimeDealDetailDataPo = null;
        if (null != tdElements && !tdElements.isEmpty()) {
            for (int i = 0; i < tdElements.size(); i++) {
                Element tdElement = tdElements.get(i);
                if (tdElement.getElementsByTag("th").isEmpty()) {
                    iFengRealtimeDealDetailDataPo = null == iFengRealtimeDealDetailDataPo ?
                            new IFengRealtimeDealDetailDataPo() : iFengRealtimeDealDetailDataPo;
                    String text = tdElement.text();
                    if (0 == i) {
                        iFengRealtimeDealDetailDataPo.setDt(
                                DateUtil.dateStrFormatChange(text, DateUtil.TIME_FORMAT_1, DateUtil.DATE_FORMAT_1)
                        );
                        iFengRealtimeDealDetailDataPo.setTime(
                                DateUtil.dateStrFormatChange(text, DateUtil.TIME_FORMAT_1, DateUtil.TIME_FORMAT_2)
                        );
                    } else if (1 == i) {
                        iFengRealtimeDealDetailDataPo.setDealType(getDealType(text));
                    } else if (2 == i) {
                        iFengRealtimeDealDetailDataPo.setPrice(BigDecimalUtil.initPrice(text));
                    } else if (3 == i) {
                        iFengRealtimeDealDetailDataPo.setDealNum(BigDecimalUtil.initLong(text));
                    } else if (4 == i) {
                        iFengRealtimeDealDetailDataPo.setDealMoney(BigDecimalUtil.initPrice(text));
                    }
                }
            }
        }
        return iFengRealtimeDealDetailDataPo;
    }
}
