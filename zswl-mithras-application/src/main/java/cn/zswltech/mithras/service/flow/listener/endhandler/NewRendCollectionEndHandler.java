package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.dto.afterlease.RentCollectionPenaltyReduceDetailRSP;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.projlifecycle.enums.ProcessEventDescEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.PenaltyReduceBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projlifecycle.mapper.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.service.afterlese.PenaltyReduceBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * @create: 2022-11-22
 **/
@Component
public class NewRendCollectionEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor{
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private PenaltyReduceBaseInfoService penaltyReduceBaseInfoService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.NewRentCollectionExemptionFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        penaltyReduceBaseInfoService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
        boolean processPass = ProcessBusinessStatusEnum.success(endContext.getEndType());
        if (processPass) {
            //减免金额
            penaltyReduceBaseInfoService.doPenaltyReduce(Long.valueOf(endContext.getBusinessKey()));
        }
        processLifecycle(endContext);
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        //全周期数据
        PenaltyReduceBaseInfo penaltyReduceBaseInfo = penaltyReduceBaseInfoService.getById(Long.valueOf(endContext.getBusinessKey()));
        if(ObjectUtil.isEmpty(penaltyReduceBaseInfo)) {
            return;
        }
        RentCollectionPenaltyReduceDetailRSP penaltyReduceDetailByBaseId = penaltyReduceBaseInfoService.getPenaltyReduceDetailByBaseId(penaltyReduceBaseInfo.getId());
        if (ObjectUtil.isEmpty(penaltyReduceDetailByBaseId) || ObjectUtil.isEmpty(penaltyReduceDetailByBaseId.getItems())){
            return;
        }
        Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfoMapper.selectBatchIds(penaltyReduceDetailByBaseId.getItems().stream().map(RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem::getContractId).collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
        penaltyReduceDetailByBaseId.getItems().forEach(e -> {
            ContractBaseInfo contractBaseInfo = contractId2Bean.get(e.getContractId());
            if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(contractBaseInfo.getProjReviewId());
                getProjIdAndProjType(endEvent, projReviewBaseInfo);
                endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("租金催收减免审批"));
            }
        });
    }
}
