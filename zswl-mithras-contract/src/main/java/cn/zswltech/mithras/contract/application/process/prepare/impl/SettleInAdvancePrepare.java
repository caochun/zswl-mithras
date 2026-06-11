package cn.zswltech.mithras.contract.application.process.prepare.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSettlePlanTypeEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.application.process.prepare.AbstractContractOperationPrepare;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起合同结清-提前结清
 */
@Component
public class SettleInAdvancePrepare extends AbstractContractOperationPrepare {
    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
        if (Objects.equals(ContractProcessStatusEnum.SETTLE_UNCOMIIT.name(), contractBaseInfo.getContractProcessStatus())) {
            return;
        }
        Assert.isTrue(ContractProcessStatusEnum.canDoStatus().contains(contractBaseInfo.getContractProcessStatus()), () -> MithrasException.newException("当前合同流状态不允许发起该操作"));
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.SETTLE_UNCOMIIT.name(), ContractSettlePlanTypeEnum.SETTLE_IN_ADVANCE.name(), contractBaseInfo.getId());
    }

    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.SETTLE_IN_ADVANCE;
    }
}
