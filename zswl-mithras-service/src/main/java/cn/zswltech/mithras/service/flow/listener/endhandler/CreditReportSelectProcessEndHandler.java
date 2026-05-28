package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.service.creditreport.CreditReportService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.CreditReportSelectFlow;

@Component
public class CreditReportSelectProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private CreditReportService creditReportService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), CreditReportSelectFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        creditReportService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
    }
}
