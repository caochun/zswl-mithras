package cn.zswltech.mithras.contract.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualRSP;
import cn.zswltech.mithras.dto.contract.rent.ContractRentEstimateListRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.contract.service.lib.contract.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @ClassName ContractRentEstimateLibHandle
 * @Description
 * @Author jackerhe
 * @Date 2022/8/23 7:26 下午
 * @Version 1.0
 **/
@Service
public class ContractRentEstimateLibHandle
        extends ContractLibAbstractHandler<ContractRentEstimateLib, ContractRentEstimate, ContractRentActualRSP> {
    @Override
    protected ContractRentEstimateLib entity2Lib(ContractRentEstimate f) {
        return BeanUtil.copyProperties(f, ContractRentEstimateLib.class);
    }

    @Override
    protected ContractRentEstimate lib2Entity(ContractRentEstimateLib t) {
        return BeanUtil.copyProperties(t, ContractRentEstimate.class);
    }

    @Override
    protected ContractRentActualRSP lib2Rsp(ContractRentEstimateLib f) {
        ContractRentActualRSP rsp = BeanUtil.copyProperties(f, ContractRentActualRSP.class);
        rsp.setDate(LocalDateTimeUtil.format(f.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setPhase(f.getCashFlowPhase());
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.RENT_ESTIMATE;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
