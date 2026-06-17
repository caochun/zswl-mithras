package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.fund.financing;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.credit.creditlimit.service.CreditLimitManagerService;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.credit.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingProcessStatus;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.credit.creditlimit.service.bo.CreditLimitReleaseBO;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.fund.application.financing.statemachine.FundFinancingBaseInfoStateMachine;
import cn.zswltech.mithras.fund.application.financing.statemachine.FundFinancingContext;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingEvent;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingLibVersionService;
import cn.zswltech.mithras.application.orchestration.finance.monthly.MonthlyStampDutyService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/22
 * @description
 */
@Component
public class FinancingCreateProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private FundFinancingBaseInfoStateMachine stateMachine;
    @Resource
    private FundFinancingLibVersionService financingLibVersionService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingPledgeInfoService financingPledgeInfoService;
    @Resource
    private MonthlyStampDutyService monthlyStampDutyService;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private CreditLimitManagerService creditLimitManagerService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;


    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.FundFinancingCreateFlow.name())
                || Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.FundFinancingModifyFlow.name());
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
                financingPledgeInfoService.sendPopUpMsg(baseInfo.getId());
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
        // 变更业务状态&固化剩余担保额度
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(baseInfo.getId());
        toUpdate.setFinancingStatus(FundFinancingStatusEnum.EFFECT.name());
        financingBaseInfoService.updateById(toUpdate);
        // 固化动态数据
//        financingBaseInfoService.fixDynamicData(baseInfo);
        // 变更流程状态
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.APPROVAL_PASS, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, null);
        // 生成数据版本
        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.APPROVAL, startUserId, processEndContext.getProcessInstanceId(),
                VersionTypeConstants.NORMAL);
        monthlyStampDutyService.saveRecordFin(baseInfo);
        if(Objects.equals(processEndContext.getModelKey(), ProcessModelTypeEnum.FundFinancingModifyFlow.name())){
            financingPlanService.updateFinancingCost(baseInfo.getId());
        }
    }

    private void doReject(ProcessEndContext processEndContext, FundFinancingBaseInfo baseInfo) {
        // 变更业务状态
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(baseInfo.getId());
        toUpdate.setFinancingStatus(FundFinancingStatusEnum.CLOSE.name());
        financingBaseInfoService.updateById(toUpdate);
        // 释放授信的占用额度
        CreditLimitReleaseBO bo = CreditLimitReleaseBO.builder()
                .bizType(CreditLimitBizTypeEnum.FUND.name())
                .bizTargetKey(String.valueOf(baseInfo.getId()))
                .amount(baseInfo.getFinancingAmount())
                .happenDate(LocalDate.now())
                .build();
        creditLimitManagerService.release(bo);
        // 固化动态数据
//        financingBaseInfoService.fixDynamicData(baseInfo);
        // 变更流程状态
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.APPROVAL_REJECT, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, null);
        // 生成数据版本
        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
    }

    private void doCancel(ProcessEndContext processEndContext, FundFinancingBaseInfo baseInfo) {
        // 变更业务状态
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(baseInfo.getId());
        toUpdate.setFinancingStatus(FundFinancingStatusEnum.CLOSE.name());
        financingBaseInfoService.updateById(toUpdate);
        CreditLimitReleaseBO bo = CreditLimitReleaseBO.builder()
                .bizType(CreditLimitBizTypeEnum.FUND.name())
                .bizTargetKey(String.valueOf(baseInfo.getId()))
                .amount(baseInfo.getFinancingAmount())
                .happenDate(LocalDate.now())
                .build();
        creditLimitManagerService.release(bo);
        // 固化动态数据
//        financingBaseInfoService.fixDynamicData(baseInfo);
        // 变更流程状态
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.NEW_WITHDRAW, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, null);
        // 生成数据版本
        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.APPROVAL, AccountUtil.getLoginInfo().getId(), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
    }
}
