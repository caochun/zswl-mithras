package cn.zswltech.mithras.application.orchestration.adapter.afterlease.workflow.listener.endhandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseProcessEndResult;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.projectprocess.flow.listener.endhandler.ILifecycleProcessor;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProcessEventDescEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.afterlease.model.CollectionPenaltyReductionInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.afterlease.application.CollectionPenaltyReductionService;
import cn.zswltech.mithras.afterlease.application.ReceiptCollectionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Optional;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.*;

/**
 * @create: 2022-11-22
 **/
@Component
public class RendCollectionEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor{
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ReceiptCollectionService receiptCollectionService;
    @Resource
    private CollectionPenaltyReductionService collectionPenaltyReductionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), RentCollectionExemptionFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        AfterLeaseProcessEndResult endResult = toEndResult(endContext.getEndType());
        receiptCollectionService.processEnd(Long.valueOf(endContext.getBusinessKey()), endResult, Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
        boolean processPass = ProcessBusinessStatusEnum.success(endContext.getEndType());
        if (processPass){
            receiptCollectionService.collectionNotice(Long.valueOf(endContext.getBusinessKey()));
        }
        processLifecycle(endContext);
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        CollectionPenaltyReductionInfo collectionPenaltyReductionInfo = collectionPenaltyReductionService.getById(Long.valueOf(endContext.getBusinessKey()));
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(collectionPenaltyReductionInfo.getContractId());
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(contractBaseInfo.getProjReviewId());
        getProjIdAndProjType(endEvent,projReviewBaseInfo);
        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("租金催收减免审批"));
    }

    private AfterLeaseProcessEndResult toEndResult(Integer endType) {
        ProcessBusinessStatusEnum status = ProcessBusinessStatusEnum.getByType(endType);
        if (ProcessBusinessStatusEnum.success(endType)) {
            return AfterLeaseProcessEndResult.PASS;
        }
        if (ProcessBusinessStatusEnum.REJECT.equals(status) || ProcessBusinessStatusEnum.REJECT_ALL.equals(status)) {
            return AfterLeaseProcessEndResult.REJECT;
        }
        if (ProcessBusinessStatusEnum.CANCEL.equals(status)) {
            return AfterLeaseProcessEndResult.CANCEL;
        }
        return AfterLeaseProcessEndResult.OTHER;
    }
}
