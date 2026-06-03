package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projlifecycle.ProcessEventDescEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.CollectionPenaltyReductionInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projlifecycle.ProjLifecycleEvent;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.afterlease.application.CollectionPenaltyReductionService;
import cn.zswltech.mithras.service.service.afterlese.ReceiptCollectionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Optional;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.*;

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
        receiptCollectionService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
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
}
