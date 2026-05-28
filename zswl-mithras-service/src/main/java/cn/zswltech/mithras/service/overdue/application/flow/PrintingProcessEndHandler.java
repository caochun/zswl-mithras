package cn.zswltech.mithras.service.overdue.application.flow;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.service.overdue.application.service.PrintingApplicationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.DocPrintingAuditFlow;

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
        printingApplicationService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
    }

}
