package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanProcessStatusEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.AfterFilingMaterialsService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckPlanVersionService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportVersionService;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author dingqi
 * @date 2022/11/21
 * @description
 */
@Slf4j
@Component
public class AfterLeaseCheckReportProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckReportVersionService afterLeaseCheckReportVersionService;
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private AfterLeaseCheckPlanVersionService afterLeaseCheckPlanVersionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.getModelKeyList().contains(endContext.getModelKey());
    }

    @Override
    public void handle(ProcessEndContext processEndContext) {
        ProcessBusinessStatusEnum pbs = ProcessBusinessStatusEnum.getByType(processEndContext.getEndType());
        if (Objects.isNull(pbs)) {
            log.warn("租后检查报告流程结束模型类型未知[{}]", JSONUtil.toJsonStr(processEndContext));
            return;
        }
        switch (pbs) {
            case PASS_ALL:
            case PASS: {
                this.doPass(processEndContext, Long.valueOf(processEndContext.getStartUserId()));
                break;
            }
            case REJECT_ALL:
            case REJECT: {
                this.doReject(processEndContext);
                break;
            }
            case CANCEL: {
                this.doCancel(processEndContext);
                break;
            }
            default: {
                // 忽略不处理
            }
        }
    }

    private void doPass(ProcessEndContext processEndContext, Long startUserId) {
        if (BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.getModelKeyList().contains(processEndContext.getModelKey())) {
            // 检查报告审批流程
            NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient = afterLeaseCheckPlanClientService.getById(Long.valueOf(processEndContext.getBusinessKey()));
            Assert.notNull(newAfterLeaseCheckPlanClient, () -> MithrasException.newException("检查计划中的客户记录信息不存在"));
            newAfterLeaseCheckPlanClient.setApprovalStatus(ProcessStatus.APPROVAL_PASS.name());
            afterLeaseCheckPlanClientService.updateById(newAfterLeaseCheckPlanClient);
            // 生成有效版本
            afterLeaseCheckReportVersionService.recordVersion(
                    newAfterLeaseCheckPlanClient.getId(),
                    VersionTypeEnum.APPROVAL,
                    startUserId,
                    processEndContext.getProcessInstanceId(),
                    VersionTypeConstants.NORMAL
            );
            boolean startAfterFilingFlag = false;
            //一般审批这里直接完结
            if (ObjectUtil.equals(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name(), processEndContext.getModelKey())) {
                // 计划完结审批流程
                NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getById(newAfterLeaseCheckPlanClient.getPlanId());
                Assert.notNull(newAfterLeaseCheckPlanBase, () -> MithrasException.newException("检查计划不存在"));
                newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.FINISH_PASS.name());
                newAfterLeaseCheckPlanBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.FINISH.name());
                newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
                afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
                afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, startUserId,
                        processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
                startAfterFilingFlag = true;
            }
            // 如果审批通过，需要将临时的【下一次检查形式】和【下一次租后检查截止日】 赋给正式的
            Map<Long, List<NewAfterLeaseCheckPlanClient>> clientMapByPlan = afterLeaseCheckPlanClientService.getClientMapByPlan(Collections.singleton(newAfterLeaseCheckPlanClient.getPlanId()));
            if (ObjectUtil.isNotEmpty(clientMapByPlan)) {
                List<NewAfterLeaseCheckPlanClient> planClients = clientMapByPlan.get(newAfterLeaseCheckPlanClient.getPlanId());
                if (ObjectUtil.isNotEmpty(planClients)) {
                    List<NewAfterLeaseCheckPlanClient> planClientList = new ArrayList<>(planClients.size());
                    for (NewAfterLeaseCheckPlanClient planClient : planClients) {
                        NewAfterLeaseCheckPlanClient newPlanClient = new NewAfterLeaseCheckPlanClient();
                        newPlanClient.setId(planClient.getId());
                        newPlanClient.setNextCheckWay(planClient.getTmpNextCheckWay());
                        newPlanClient.setNextDeadline(planClient.getTmpNextDeadline());
                        if(!AfterLeaseCheckWayEnum.WITHOUT_CHECK.name().equals(planClient.getTmpNextCheckWay())
                                && Objects.nonNull(planClient.getTmpNextDeadline())){
                            newPlanClient.setNextCheckFlag(0);
                        }
                        planClientList.add(newPlanClient);
                    }
                    afterLeaseCheckPlanClientService.updateBatchById(planClientList);
                }
            }
            if (startAfterFilingFlag) {
                startAfterFiling(newAfterLeaseCheckPlanClient.getId(),processEndContext.getProcessInstanceId());
            }
        }

    }

    private void doReject(ProcessEndContext processEndContext) {
        if (BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.getModelKeyList().contains(processEndContext.getModelKey())) {
            // 检查报告审批流程
            NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient = afterLeaseCheckPlanClientService.getById(Long.valueOf(processEndContext.getBusinessKey()));
            Assert.notNull(newAfterLeaseCheckPlanClient, () -> MithrasException.newException("检查计划中的客户记录信息不存在"));
            newAfterLeaseCheckPlanClient.setApprovalStatus(ProcessStatus.APPROVAL_REJECT.name());
            afterLeaseCheckPlanClientService.updateById(newAfterLeaseCheckPlanClient);
            // 生成无效版本
            afterLeaseCheckReportVersionService.recordVersion(
                    newAfterLeaseCheckPlanClient.getId(),
                    VersionTypeEnum.APPROVAL,
                    AccountUtil.getLoginInfo().getId(),
                    processEndContext.getProcessInstanceId(),
                    VersionTypeConstants.INVALID
            );
        }
        if (Objects.equals(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name(), processEndContext.getModelKey())) {
            // 一般检查报告流程关闭需要同步关闭任务
            this.commonlySpecialAction(Long.valueOf(processEndContext.getBusinessKey()));
        }
    }

    private void doCancel(ProcessEndContext processEndContext) {
        if (BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.getModelKeyList().contains(processEndContext.getModelKey())) {
            // 检查报告审批流程
            NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient = afterLeaseCheckPlanClientService.getById(Long.valueOf(processEndContext.getBusinessKey()));
            Assert.notNull(newAfterLeaseCheckPlanClient, () -> MithrasException.newException("检查计划中的客户记录信息不存在"));
            newAfterLeaseCheckPlanClient.setApprovalStatus(ProcessStatus.CANCELED.name());
            afterLeaseCheckPlanClientService.updateById(newAfterLeaseCheckPlanClient);
            // 生成无效版本
            afterLeaseCheckReportVersionService.recordVersion(
                    newAfterLeaseCheckPlanClient.getId(),
                    VersionTypeEnum.APPROVAL,
                    AccountUtil.getLoginInfo().getId(),
                    processEndContext.getProcessInstanceId(),
                    VersionTypeConstants.INVALID
            );
        }
        if (Objects.equals(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name(), processEndContext.getModelKey())) {
            // 一般检查报告流程关闭需要同步关闭任务
            this.commonlySpecialAction(Long.valueOf(processEndContext.getBusinessKey()));
        }
    }

    private void commonlySpecialAction(Long checkPlanClientId) {
        NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient = afterLeaseCheckPlanClientService.getById(checkPlanClientId);
        if (Objects.nonNull(newAfterLeaseCheckPlanClient)) {
            afterLeaseCheckPlanBaseService.close(newAfterLeaseCheckPlanClient.getPlanId());
        }
    }

    private void startAfterFiling(Long objectId,String processInstanceId){
        // 生产租后资料归档流程
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ThreadPoolUtil.getCommonPool().execute(() -> {
                    try {
                        SpringUtil.getBean(AfterFilingMaterialsService.class).initFilingMaterials(objectId,processInstanceId);
                    } catch (Exception e) {
                        log.error("异步发起租后资料归档流程发生异常[objectId:{}]", objectId, e);
                    }
                });
            }
        });
    }
}
