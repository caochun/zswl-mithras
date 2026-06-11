package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.projectprocess.flow.listener.endhandler.ILifecycleProcessor;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProcessEventDescEnum;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ProjReviewPricingApprovalFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow;

/**
 * 项目定价评审流程结束
 */
@Component
public class ProjPricingProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor {
    @Resource
    private ProjPricingService projPricingService;
    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProjReviewPricingApprovalFlow.name(),
                ProjReviewPricingModifyApprovalFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        projPricingService.processEnd(Long.valueOf(endContext.getBusinessKey()),
                endContext.getEndType(), Long.valueOf(endContext.getStartUserId()),
                endContext.getProcessInstanceId(), endContext.getModelKey());
        processLifecycle(endContext);
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(Long.valueOf(endContext.getBusinessKey()));
        getProjPricingIdAndProjType(endEvent, projPricingBaseInfo);
        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("项目评审定价审批"));
    }
}
