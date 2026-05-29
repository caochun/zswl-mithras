package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckPlanProcessStatusEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckPlanVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/11/14
 * @description
 */
@Slf4j
@Component
public class AfterLeaseCheckPlanProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private AfterLeaseCheckPlanVersionService afterLeaseCheckPlanVersionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN.getModelKeyList().contains(endContext.getModelKey());
    }

    @Override
    public void handle(ProcessEndContext processEndContext) {
        ProcessBusinessStatusEnum pbs = ProcessBusinessStatusEnum.getByType(processEndContext.getEndType());
        if (Objects.isNull(pbs)) {
            log.warn("租后检查计划流程结束模型类型未知[{}]", JSONUtil.toJsonStr(processEndContext));
            return;
        }
        switch (pbs) {
            case PASS_ALL:
            case PASS: {
                this.doPass(processEndContext, Long.valueOf(processEndContext.getStartUserId()));
                break;
            }
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
        if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name().equals(processEndContext.getModelKey())) {
            // 新建计划发布审批流程
            NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getById(Long.valueOf(processEndContext.getBusinessKey()));
            Assert.notNull(newAfterLeaseCheckPlanBase, () -> MithrasException.newException("检查计划不存在"));
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.NEW_PASS.name());
            newAfterLeaseCheckPlanBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.PUBLISH.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, startUserId,
                    processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
        } else if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishModifyFlow.name().equals(processEndContext.getModelKey())) {
            // 变更计划发布审批流程
            NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getById(Long.valueOf(processEndContext.getBusinessKey()));
            Assert.notNull(newAfterLeaseCheckPlanBase, () -> MithrasException.newException("检查计划不存在"));
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.MODIFY_PASS.name());
            newAfterLeaseCheckPlanBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.PUBLISH.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, startUserId,
                    processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
        } else if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanFinishFlow.name().equals(processEndContext.getModelKey())) {
            // 计划完结审批流程
            NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getById(Long.valueOf(processEndContext.getBusinessKey()));
            Assert.notNull(newAfterLeaseCheckPlanBase, () -> MithrasException.newException("检查计划不存在"));
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.FINISH_PASS.name());
            newAfterLeaseCheckPlanBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.FINISH.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, startUserId,
                    processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
        }
    }

    private void doReject(ProcessEndContext processEndContext) {
        NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getById(Long.valueOf(processEndContext.getBusinessKey()));
        Assert.notNull(newAfterLeaseCheckPlanBase, () -> MithrasException.newException("检查计划不存在"));
        if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name().equals(processEndContext.getModelKey())) {
            // 新建计划发布审批流程
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.NEW_REJECT.name());
            newAfterLeaseCheckPlanBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.NEW.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            afterLeaseCheckPlanVersionService.reset(newAfterLeaseCheckPlanBase.getId());
        } else if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishModifyFlow.name().equals(processEndContext.getModelKey())) {
            // 变更计划发布审批流程
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.MODIFY_REJECT.name());
            newAfterLeaseCheckPlanBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.MODIFY.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            afterLeaseCheckPlanVersionService.reset(newAfterLeaseCheckPlanBase.getId());
        } else if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanFinishFlow.name().equals(processEndContext.getModelKey())) {
            // 计划完结审批流程
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.FINISH_REJECT.name());
            newAfterLeaseCheckPlanBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.CHECKING.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            // TODO 版本改造check
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            // 特殊场景，子表数据有自己的审批流，此处拒绝，主表数据本就和版本表一致，子表数据和版本表不一致（在主表完结审批通过的时候会生成版本），所以不回退版本
//            afterLeaseCheckPlanVersionService.reset(afterLeaseCheckPlanBase);
        }
    }

    private void doCancel(ProcessEndContext processEndContext) {
        NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getById(Long.valueOf(processEndContext.getBusinessKey()));
        Assert.notNull(newAfterLeaseCheckPlanBase, () -> MithrasException.newException("检查计划不存在"));
        if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name().equals(processEndContext.getModelKey())) {
            // 新建计划发布审批流程
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.NEW_CANCEL.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            afterLeaseCheckPlanVersionService.reset(newAfterLeaseCheckPlanBase.getId());
        } else if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishModifyFlow.name().equals(processEndContext.getModelKey())) {
            // 变更计划发布审批流程
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.MODIFY_CANCEL.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            afterLeaseCheckPlanVersionService.reset(newAfterLeaseCheckPlanBase.getId());
        } else if (ProcessModelTypeEnum.NewAfterLeaseCheckPlanFinishFlow.name().equals(processEndContext.getModelKey())) {
            // 计划完结审批流程
            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.FINISH_CANCEL.name());
            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
            // TODO 版本改造check
            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            // 特殊场景，子表数据有自己的审批流，此处拒绝，主表数据本就和版本表一致，子表数据和版本表不一致（在主表完结审批通过的时候会生成版本），所以不回退版本
//            afterLeaseCheckPlanVersionService.reset(afterLeaseCheckPlanBase);
        }
    }
}
