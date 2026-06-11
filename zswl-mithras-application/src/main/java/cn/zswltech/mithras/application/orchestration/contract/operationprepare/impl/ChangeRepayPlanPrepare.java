package cn.zswltech.mithras.application.orchestration.contract.operationprepare.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseAdjustEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.afterlease.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.application.orchestration.contract.operationprepare.AbstractContractChangePrepare;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起合同变更-调整还款计划
 */
@Component
public class ChangeRepayPlanPrepare extends AbstractContractChangePrepare {
    @Resource
    private AfterLeaseAdjustInfoService afterLeaseAdjustInfoService;

    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
        AfterLeaseAdjustInfo afterLeaseAdjustInfo = afterLeaseAdjustInfoService.adjustBaseLastByprojId(contractBaseInfo.getProjReviewId(), AfterLeaseAdjustEnum.REPAYMENT.name());
        Assert.notNull(afterLeaseAdjustInfo, () -> MithrasException.newException("请先在【租后管理】申请项目调整！"));
        super.doPrepare(contractBaseInfo);
    }

    @Override
    protected ContractChangeTypeEnum contractChangeType() {
        return ContractChangeTypeEnum.CHANGE_REPAY_PLAN;
    }

    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.CHANGE_REPAYMENT_PLAN;
    }
}
