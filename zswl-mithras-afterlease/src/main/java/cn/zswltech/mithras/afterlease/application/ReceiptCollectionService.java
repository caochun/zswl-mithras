package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


//借据卡-逾期情况
public interface ReceiptCollectionService {

    //借据卡逾期汇总
    Page<CollectionBaseInfo> list(ReceiptCollectionListREQ req);

    //分配减免金额，不再通知苍穹
    void collectionNotice(Long reduceId);

    //逾期催收
    CollectionOverdueRSP overdue(CollectionOverdueREQ req);

    void effect(CollectionPenaltyReductionEffectREQ req);

    void modify(CollectionPenaltyReductionModifyREQ req);

    void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId);

    Long calculationInterest(Long contractId);
}
