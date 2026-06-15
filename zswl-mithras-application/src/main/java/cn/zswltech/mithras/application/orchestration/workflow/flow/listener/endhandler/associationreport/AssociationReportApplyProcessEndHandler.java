package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.associationreport;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationProcessStatusEnum;
import cn.zswltech.mithras.associationreport.service.AssociationReportService;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.*;

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
        ProcessBusinessStatusEnum processBusinessStatus = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        associationReportService.processEnd(
                Long.valueOf(endContext.getBusinessKey()),
                toAssociationProcessStatus(processBusinessStatus),
                ProcessBusinessStatusEnum.success(endContext.getEndType()),
                processBusinessStatus == ProcessBusinessStatusEnum.PASS || processBusinessStatus == ProcessBusinessStatusEnum.PASS_ALL,
                Long.valueOf(endContext.getStartUserId()),
                endContext.getProcessInstanceId());
    }

    private AssociationProcessStatusEnum toAssociationProcessStatus(ProcessBusinessStatusEnum processBusinessStatus) {
        switch (processBusinessStatus) {
            case PASS:
            case PASS_ALL:
                return AssociationProcessStatusEnum.APPROVAL_PASS;
            case REJECT:
            case REJECT_ALL:
                return AssociationProcessStatusEnum.APPROVAL_REJECT;
            case CANCEL:
                return AssociationProcessStatusEnum.CANCEL;
            default:
                return null;
        }
    }

}
