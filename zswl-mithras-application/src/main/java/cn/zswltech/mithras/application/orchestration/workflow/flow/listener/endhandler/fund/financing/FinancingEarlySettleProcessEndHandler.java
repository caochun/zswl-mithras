package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.fund.financing;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingChangeSubTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingProcessStatus;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.statemachine.FundFinancingBaseInfoStateMachine;
import cn.zswltech.mithras.fund.application.financing.statemachine.FundFinancingContext;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingEvent;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingLibVersionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/23
 * @description
 */
@Component
public class FinancingEarlySettleProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private FundFinancingBaseInfoStateMachine stateMachine;
    @Resource
    private FundFinancingLibVersionService financingLibVersionService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.FundFinancingEarlySettleFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        Long financingId = Long.valueOf(endContext.getBusinessKey());
        FundFinancingBaseInfo baseInfo = financingBaseInfoService.getById(financingId);
        Assert.notNull(baseInfo, () -> MithrasException.newException("融资数据不存在"));
        ProcessBusinessStatusEnum pbs = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        switch (pbs) {
            case PASS:
            case PASS_ALL: {
                this.doPass(endContext, baseInfo, Long.valueOf(endContext.getStartUserId()));
                break;
            }
            case REJECT:
            case REJECT_ALL: {
                this.doReject(endContext, baseInfo);
                break;
            }
            case CANCEL: {
                this.doCancel(endContext, baseInfo);
                break;
            }
            default: {
                throw new MithrasException("非法的流程状态");
            }
        }
    }

    private void doPass(ProcessEndContext processEndContext, FundFinancingBaseInfo baseInfo, Long startUserId) {
        // 变更业务状态
//        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
//        toUpdate.setId(baseInfo.getId());
//        toUpdate.setFinancingStatus(FundFinancingStatusEnum.SETTLE.name());
//        financingBaseInfoService.updateById(toUpdate);
        // 固化动态数据
//        financingBaseInfoService.fixDynamicData(baseInfo);
        // 变更流程状态
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.APPROVAL_PASS, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_EARLY_SETTLE.name());
        // 生成数据版本
        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.APPROVAL, startUserId, processEndContext.getProcessInstanceId(),
                VersionTypeConstants.NORMAL);
        // 通知收付款模块尝试更新还款表
        receiptRepayBaseInfoService.processFinancingEffectEnd(baseInfo.getId());
    }

    private void doReject(ProcessEndContext processEndContext, FundFinancingBaseInfo baseInfo) {
        // 固化动态数据
//        financingBaseInfoService.fixDynamicData(baseInfo);
        // 变更流程状态
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.APPROVAL_REJECT, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_EARLY_SETTLE.name());
        // 生成数据版本
        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
        // 回退数据
        financingLibVersionService.reset(baseInfo.getMainId());
        // 修复被回滚的流程状态
        stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_EARLY_SETTLE.name());
    }

    private void doCancel(ProcessEndContext processEndContext, FundFinancingBaseInfo baseInfo) {
        // 固化动态数据
//        financingBaseInfoService.fixDynamicData(baseInfo);
        // 变更流程状态
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.NEW_WITHDRAW, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_EARLY_SETTLE.name());
        // 生成数据版本
        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
        // 回退数据
        financingLibVersionService.reset(baseInfo.getMainId());
        // 修复被回滚的流程状态
        stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_EARLY_SETTLE.name());
    }
}
