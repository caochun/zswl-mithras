package cn.zswltech.mithras.service.service.contract.operationprepare.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseAdjustEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.service.mapper.model.afterlease.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.service.service.contract.operationprepare.AbstractContractChangePrepare;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起合同变更-展期
 */
@Component
public class ChangeExtensionPrepare extends AbstractContractChangePrepare {
    @Resource
    private AfterLeaseAdjustInfoService afterLeaseAdjustInfoService;

    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
        AfterLeaseAdjustInfo afterLeaseAdjustInfo = afterLeaseAdjustInfoService.adjustBaseLastByprojId(contractBaseInfo.getProjReviewId(), AfterLeaseAdjustEnum.EXTEND.name());
        Assert.notNull(afterLeaseAdjustInfo, () -> MithrasException.newException("请先在【租后管理】申请项目调整！"));
        super.doPrepare(contractBaseInfo);
    }

    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.CHANGE_EXTENSION;
    }

    @Override
    protected ContractChangeTypeEnum contractChangeType() {
        return ContractChangeTypeEnum.EXTENSION;
    }
}
