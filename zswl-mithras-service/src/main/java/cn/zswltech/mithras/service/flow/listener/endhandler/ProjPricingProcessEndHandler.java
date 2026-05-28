package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.enums.projlifecycle.ProcessEventDescEnum;
import cn.zswltech.mithras.service.mapper.model.projlifecycle.ProjLifecycleEvent;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.ProjReviewPricingApprovalFlow;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow;

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
