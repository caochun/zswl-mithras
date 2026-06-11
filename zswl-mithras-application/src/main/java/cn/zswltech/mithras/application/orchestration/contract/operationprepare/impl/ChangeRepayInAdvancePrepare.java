package cn.zswltech.mithras.application.orchestration.contract.operationprepare.impl;

import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.application.orchestration.contract.operationprepare.AbstractContractChangePrepare;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起合同变更-提前还款
 */
@Component
public class ChangeRepayInAdvancePrepare extends AbstractContractChangePrepare {
    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.CHANGE_REPAYMENT_IN_ADVANCE;
    }

    @Override
    protected ContractChangeTypeEnum contractChangeType() {
        return ContractChangeTypeEnum.EARLY_REPAYMENT;
    }
}
