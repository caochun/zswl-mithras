package cn.zswltech.mithras.application.orchestration.adapter.afterlease.workflow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseProcessEndResult;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/19 11:21
 */
@Component
public class AfterLeaseCheckExternalQueryProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private AfterLeaseCheckExternalQueryService externalQueryService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return ProcessModelTypeEnum.NewAfterLeaseCheckExternalQueryFlow.name().equals(endContext.getModelKey());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        externalQueryService.processEnd(Long.valueOf(endContext.getBusinessKey()), toEndResult(endContext.getEndType()), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
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
