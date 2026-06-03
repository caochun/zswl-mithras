package cn.zswltech.mithras.collection.service.bo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 收款详情调用链
 */
@Data
public class CollectionDetailChainBO {

    /**
     * 计划收款日期
     */
    private LocalDate planCollectionDate;

    /**
     * 实收日期
     */
    private LocalDate collectionDate;

    /**
     * 是否逾期 true逾期
     **/
    private boolean isOverdue;

    private boolean isNormal;

    private CollectionDetailChainBO next;

}
