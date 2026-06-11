package cn.zswltech.mithras.afterlease.controller;

import cn.zswltech.mithras.afterlease.application.ReceiptCollectionApplicationService;
import cn.zswltech.mithras.api.afterlease.ReceiptCollectionApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.CollectionOverdueREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionOverdueRSP;
import cn.zswltech.mithras.dto.afterlease.CollectionPenaltyReductionEffectREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionPenaltyReductionInfoListREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionPenaltyReductionInfoListRSP;
import cn.zswltech.mithras.dto.afterlease.CollectionPenaltyReductionModifyREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionRelationContractREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionRelationContractRSP;
import cn.zswltech.mithras.dto.afterlease.ReceiptCollectionListREQ;
import cn.zswltech.mithras.dto.afterlease.ReceiptCollectionListRSP;
import cn.zswltech.mithras.dto.afterlease.ReceiptReduceInterestRSP;
import cn.zswltech.mithras.dto.afterlease.ReceiptReduceInterstREQ;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class ReceiptCollectionController implements ReceiptCollectionApi {

    @Resource
    private ReceiptCollectionApplicationService receiptCollectionApplicationService;

    @Override
    public R<PageR<ReceiptCollectionListRSP>> list(@Valid ReceiptCollectionListREQ req) {
        return receiptCollectionApplicationService.list(req);
    }

    @Override
    public R<CollectionOverdueRSP> overdue(@Valid CollectionOverdueREQ req) {
        return receiptCollectionApplicationService.overdue(req);
    }

    @Override
    public R<Void> effect(@Valid CollectionPenaltyReductionEffectREQ req) {
        return receiptCollectionApplicationService.effect(req);
    }

    @Override
    public R<Void> modify(@Valid CollectionPenaltyReductionModifyREQ req) {
        return receiptCollectionApplicationService.modify(req);
    }

    @Override
    public R<PageR<CollectionPenaltyReductionInfoListRSP>> reductionList(@Valid CollectionPenaltyReductionInfoListREQ req) {
        return receiptCollectionApplicationService.reductionList(req);
    }

    @Override
    public R<CollectionRelationContractRSP> relationContract(@Valid CollectionRelationContractREQ req) {
        return receiptCollectionApplicationService.relationContract(req);
    }

    @Override
    public R<ReceiptReduceInterestRSP> calculationInterest(@Valid ReceiptReduceInterstREQ req) {
        return receiptCollectionApplicationService.calculationInterest(req);
    }
}
