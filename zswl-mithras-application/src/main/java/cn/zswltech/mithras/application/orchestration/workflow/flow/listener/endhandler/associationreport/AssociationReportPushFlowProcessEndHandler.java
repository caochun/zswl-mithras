package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.associationreport;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.associationreport.service.AssociationReportService;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationProcessStatusEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.associationreport.service.lib.association.impl.AssociationReportVersionServiceServiceImpl;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/10/27
 * @description 金融局报送-上报流程结束处理器
 */
@Slf4j
@Component
public class AssociationReportPushFlowProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private AssociationReportService associationReportService;
    @Resource
    private AssociationReportVersionServiceServiceImpl associationReportVersionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return StrUtil.equals(endContext.getModelKey(), ProcessModelTypeEnum.AssociationReportPushFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        Long applyId = Long.parseLong(endContext.getBusinessKey());
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        switch (processBusinessStatusEnum) {
            case PASS:
            case PASS_ALL: {
                associationReportVersionService.recordVersion(applyId, VersionTypeEnum.APPROVAL, Long.parseLong(endContext.getStartUserId()), endContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        ThreadPoolUtil.getCommonPool().execute(() -> {
                            try {
                                associationReportService.updatePushProcessStatus(applyId, AssociationProcessStatusEnum.APPROVAL_PASS);
                                associationReportService.push(applyId);
                            } catch (Exception e) {
                                log.error("金融局报送上报审批通过，异步上报数据发生异常[processInstanceId:{}]", endContext.getProcessInstanceId());
                            }
                        });
                    }
                });
                break;
            }
            case REJECT:
            case REJECT_ALL: {
                associationReportVersionService.recordVersion(applyId, VersionTypeEnum.APPROVAL, Long.parseLong(endContext.getStartUserId()), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                associationReportService.updatePushProcessStatus(applyId, AssociationProcessStatusEnum.APPROVAL_REJECT);
                break;
            }
            case CANCEL: {
                associationReportVersionService.recordVersion(applyId, VersionTypeEnum.APPROVAL, Long.parseLong(endContext.getStartUserId()), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                associationReportService.updatePushProcessStatus(applyId, AssociationProcessStatusEnum.CANCEL);
                break;
            }
        }
    }
}
