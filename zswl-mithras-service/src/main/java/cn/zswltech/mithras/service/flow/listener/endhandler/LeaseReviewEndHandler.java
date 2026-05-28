package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseReviewService;
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
        return BusinessModuleEnum.LEASE_TEXT.getModelKeyList().contains(endContext.getModelKey());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        leaseReviewService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType());
    }

}
