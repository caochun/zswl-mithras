package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
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
        externalQueryService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
    }

}
