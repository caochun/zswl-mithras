package cn.zswltech.mithras.service.service.contract.operationprepare.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.operationprepare.AbstractContractOperationPrepare;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起新增借据
 */
@Component
public class NewReceiptPrepare extends AbstractContractOperationPrepare {
    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
//        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
//            throw new MithrasException("仅直租合同支持手动新增投放");
//        }
        if (Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.NEW_RECEIPT_UNCOMMIT.name())) {
            return;
        }
        Assert.isTrue(ContractProcessStatusEnum.canDoStatus().contains(contractBaseInfo.getContractProcessStatus()), () -> MithrasException.newException("当前合同流状态不允许发起该操作"));
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.NEW_RECEIPT_UNCOMMIT.name(), null, contractBaseInfo.getId());
    }

    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.NEW_RECEIPT;
    }
}
