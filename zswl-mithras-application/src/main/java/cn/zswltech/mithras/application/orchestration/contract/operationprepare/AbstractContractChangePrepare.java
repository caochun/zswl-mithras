package cn.zswltech.mithras.application.orchestration.contract.operationprepare;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.contract.ContractService;
import cn.zswltech.mithras.contract.application.process.prepare.AbstractContractOperationPrepare;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description
 */
public abstract class AbstractContractChangePrepare extends AbstractContractOperationPrepare {
    @Resource
    protected ContractService contractService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void prepare(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同数据不存在"));
        boolean check = contractService.checkCanDoChange(contractId, this.contractChangeType().name());
        Assert.isTrue(check, () -> MithrasException.newException("不存在生效借据，不允许进行合同变更操作"));
        this.doPrepare(contractBaseInfo);
    }

    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
        if (Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.CHANGE_UNCOMMIT.name())) {
            return;
        }
        Assert.isTrue(ContractProcessStatusEnum.canDoStatus().contains(contractBaseInfo.getContractProcessStatus()), () -> MithrasException.newException("当前合同流状态不允许发起该操作"));
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), this.contractChangeType().name(), contractBaseInfo.getId());
    }

    protected abstract ContractChangeTypeEnum contractChangeType();
}
