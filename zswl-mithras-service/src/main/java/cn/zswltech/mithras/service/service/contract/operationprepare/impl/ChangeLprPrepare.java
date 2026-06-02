package cn.zswltech.mithras.service.service.contract.operationprepare.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.service.enums.projestablish.RateType;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.contract.operationprepare.AbstractContractChangePrepare;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起合同变更-LPR
 */
@Component
public class ChangeLprPrepare extends AbstractContractChangePrepare {
    @Resource
    private ContractPriceService contractPriceService;

    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
        ContractPriceDetailREQ req = new ContractPriceDetailREQ();
        req.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP rsp = contractPriceService.detail(req);
        String rateType = rsp.getRateType();
        Assert.isTrue(!Objects.equals(rateType, RateType.FIXED.name()), () -> MithrasException.newException("固定利率，不能进行LPR变更"));
        super.doPrepare(contractBaseInfo);
    }

    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.CHANGE_LPR;
    }

    @Override
    protected ContractChangeTypeEnum contractChangeType() {
        return ContractChangeTypeEnum.LPR_CHANGE;
    }
}
