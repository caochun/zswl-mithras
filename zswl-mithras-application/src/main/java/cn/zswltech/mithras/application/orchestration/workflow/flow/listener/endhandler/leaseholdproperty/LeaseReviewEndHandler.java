package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.leaseholdproperty;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.leaseholdproperty.application.review.LeaseReviewService;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 租赁物流程结束
 */
@Component
public class LeaseReviewEndHandler extends AbstractProcessEndHandler {

    @Resource
    private LeaseReviewService leaseReviewService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return ProcessModelTypeEnum.LeaseCreateFlow.name().equals(endContext.getModelKey())
                || ProcessModelTypeEnum.LeaseModifyFlow.name().equals(endContext.getModelKey());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        leaseReviewService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType());
    }
}
