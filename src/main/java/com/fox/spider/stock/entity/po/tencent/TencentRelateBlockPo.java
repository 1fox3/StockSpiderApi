package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;

/**
 * 股票所属板块
 *
 * @author lusongsong
 * @date 2020/12/29 18:17
 */
@Data
public class TencentRelateBlockPo implements Serializable {
    /**
     * 板块代码
     */
    String code;
    /**
     * 板块名称
     */
    String name;
}
