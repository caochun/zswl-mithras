package cn.zswltech.mithras.service.overdue.application.flow;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.contract.overdue.application.docprinting.PrintingApplicationService;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.DocPrintingAuditFlow;

/**
 * @author zhaozhengkang
 */
@Component
public class PrintingProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private PrintingApplicationService printingApplicationService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), DocPrintingAuditFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        boolean processPass = ProcessBusinessStatusEnum.success(endContext.getEndType());
        boolean processCancel = ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType());
        printingApplicationService.processEnd(Long.valueOf(endContext.getBusinessKey()), processPass, processCancel,
                Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
    }

}
