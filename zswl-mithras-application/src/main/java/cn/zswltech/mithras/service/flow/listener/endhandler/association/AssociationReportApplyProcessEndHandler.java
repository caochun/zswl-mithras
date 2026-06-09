package cn.zswltech.mithras.service.flow.listener.endhandler.association;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.associationreport.service.AssociationReportService;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.*;

@Component
public class AssociationReportApplyProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private AssociationReportService associationReportService;


    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), AssociationReportRealtimeFlow.name(), AssociationReportCaseTypeRelatedFlow.name(), AssociationReportQuarterMonthFlow.name(),AssociationReportMainBusinessFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        //更新子表流程状态
        associationReportService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
    }

}
