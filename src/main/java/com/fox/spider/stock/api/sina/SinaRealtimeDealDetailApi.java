package com.fox.spider.stock.api.sina;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealDetailPo;
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
 * 新浪网股票最新交易日交易明细
 *
 * @author lusongsong
 * @date 2021/1/8 16:51
 */
@Component
public class SinaRealtimeDealDetailApi extends SinaBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * A股接口地址
     * https://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradedetail.php?symbol=sh603383&page=36
     */
    private static final String API_URL =
            "https://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradedetail.php?symbol=";

    /**
     * 获取页面地址
     *
     * @param stockVo
     * @param page
     * @return
     */
    private String getApiUrl(StockVo stockVo, Integer page) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(API_URL);
        stringBuilder.append((String) sinaStockCode(stockVo));
        stringBuilder.append("&page=");
        stringBuilder.append(page);
        return stringBuilder.toString();
    }

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
     * 去除百分号
     *
     * @param text
     * @return
     */
    private String clearText(String text) {
        return text.replace("%", "").replace(",", "");
    }

    /**
     * 获取交易明细数据数据
     *
     * @param stockVo
     * @return
     */
    public List<SinaRealtimeDealDetailPo> dealDetail(StockVo stockVo, Integer page) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()
                || null == page || page < 0) {
            return null;
        }
        try {
            Document document = Jsoup.connect(getApiUrl(stockVo, page)).get();
            Element tableElement = document.getElementById("datatbl");
            Elements tbodyElements = tableElement.getElementsByTag("tbody");
            Element tbodyElement = tbodyElements.first();
            return handleResponse(tbodyElement);
        } catch (IOException e) {
            logger.error(stockVo.toString() + page.toString(), e);
        }
        return null;
    }

    /**
     * 处理返回数据
     *
     * @param tbodyElement
     * @return
     */
    private List<SinaRealtimeDealDetailPo> handleResponse(Element tbodyElement) {
        List<SinaRealtimeDealDetailPo> sinaRealtimeDealDetailPoList = null;
        if (null != tbodyElement) {
            Elements trElements = tbodyElement.getElementsByTag("tr");
            if (null != trElements && !trElements.isEmpty()) {
                for (Element trElement : trElements) {
                    if (null != trElement) {
                        Elements tdElements = trElement.children();
                        SinaRealtimeDealDetailPo sinaRealtimeDealDetailPo = handleRow(tdElements);
                        if (null != sinaRealtimeDealDetailPo) {
                            sinaRealtimeDealDetailPoList = null == sinaRealtimeDealDetailPoList ?
                                    new ArrayList<>() : sinaRealtimeDealDetailPoList;
                            sinaRealtimeDealDetailPoList.add(sinaRealtimeDealDetailPo);
                        }
                    }
                }
            }
        }
        return sinaRealtimeDealDetailPoList;
    }

    /**
     * 处理单条交易明细
     *
     * @param tdElements
     * @return
     */
    private SinaRealtimeDealDetailPo handleRow(Elements tdElements) {
        SinaRealtimeDealDetailPo sinaRealtimeDealDetailPo = null;
        if (null != tdElements && !tdElements.isEmpty()) {
            sinaRealtimeDealDetailPo = new SinaRealtimeDealDetailPo();
            for (int i = 0; i < tdElements.size(); i++) {
                Element tdElement = tdElements.get(i);
                String text = tdElement.text();
                text = clearText(text);
                if (0 == i) {
                    sinaRealtimeDealDetailPo.setTime(text);
                } else if (1 == i) {
                    sinaRealtimeDealDetailPo.setPrice(BigDecimalUtil.initPrice(text));
                } else if (2 == i) {
                    sinaRealtimeDealDetailPo.setUptickRate(BigDecimalUtil.initRate(text));
                } else if (3 == i) {
                    try {
                        sinaRealtimeDealDetailPo.setUptickPrice(BigDecimalUtil.initPrice(text));
                    } catch (NumberFormatException e) {
                        sinaRealtimeDealDetailPo.setUptickPrice(BigDecimalUtil.initPrice("0"));
                    }
                } else if (4 == i) {
                    sinaRealtimeDealDetailPo.setDealNum(BigDecimalUtil.initLong(text));
                } else if (5 == i) {
                    sinaRealtimeDealDetailPo.setDealMoney(BigDecimalUtil.initPrice(text));
                } else if (6 == i) {
                    sinaRealtimeDealDetailPo.setDealType(getDealType(text));
                }
            }
        }
        return sinaRealtimeDealDetailPo;
    }
}
