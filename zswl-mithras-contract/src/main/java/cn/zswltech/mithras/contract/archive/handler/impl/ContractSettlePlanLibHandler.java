package cn.zswltech.mithras.contract.archive.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.settle.ContractSettlePlanDetailRSP;
import cn.zswltech.mithras.contract.convert.contract.ContractSettlePlanConvert;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSettlePlan;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSettlePlanLib;
import cn.zswltech.mithras.contract.archive.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Component
public class ContractSettlePlanLibHandler extends ContractLibAbstractHandler<ContractSettlePlanLib, ContractSettlePlan, ContractSettlePlanDetailRSP> {
    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.SETTLE_PLAN;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected ContractSettlePlanLib entity2Lib(ContractSettlePlan f) {
        return BeanUtil.copyProperties(f, ContractSettlePlanLib.class);
    }

    @Override
    protected ContractSettlePlan lib2Entity(ContractSettlePlanLib t) {
        return BeanUtil.copyProperties(t, ContractSettlePlan.class);
    }

    @Override
    protected ContractSettlePlanDetailRSP lib2Rsp(ContractSettlePlanLib f) {
        return ContractSettlePlanConvert.toContractSettlePlanDetailRSP(f);
    }
}
