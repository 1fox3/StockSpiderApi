package com.fox.spider.stock.entity.po.ifeng;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 凤凰网交易明细信息
 *
 * @author lusongsong
 * @date 2021/1/7 17:27
 */
@Data
public class IFengRealtimeDealDetailPo implements Serializable {
    /**
     * 总页数
     */
    Integer totalPageNum;
    /**
     * 交易明细列表
     */
    List<IFengRealtimeDealDetailDataPo> detailInfoList;
}
