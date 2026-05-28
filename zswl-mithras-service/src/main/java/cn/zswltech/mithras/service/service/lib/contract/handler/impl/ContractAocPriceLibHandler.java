package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.contract.price.ContractAocPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.StructuredInterest;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAocPriceLib;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.service.util.FinancialUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@Service
public class ContractAocPriceLibHandler extends ContractLibAbstractHandler<ContractAocPriceLib, ContractAocPrice, ContractAocPriceDetailRSP> {
    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.ZR_PRICE;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected ContractAocPriceLib entity2Lib(ContractAocPrice f) {
        return BeanUtil.copyProperties(f, ContractAocPriceLib.class);
    }

    @Override
    protected ContractAocPrice lib2Entity(ContractAocPriceLib t) {
        return BeanUtil.copyProperties(t, ContractAocPrice.class);
    }

    @Override
    protected ContractAocPriceDetailRSP lib2Rsp(ContractAocPriceLib f) {
        ContractAocPriceDetailRSP rsp = BeanUtil.copyProperties(f, ContractAocPriceDetailRSP.class);
        if (Objects.nonNull(rsp)) {
            rsp.setId(f.getOriginId());
            if (Objects.nonNull(rsp.getContractAmount()) && Objects.nonNull(rsp.getEarnestMoney())) {
                rsp.setEarnestMoneyRate(FinancialUtil.calculateFeeRate(rsp.getEarnestMoney(), rsp.getContractAmount()));
            }
            if (Objects.nonNull(rsp.getContractAmount()) && Objects.nonNull(rsp.getConsultingFee())) {
                rsp.setConsultingFeeRate(FinancialUtil.calculateFeeRate(rsp.getConsultingFee(), rsp.getContractAmount()));
            }
            if(ObjectUtil.isNotEmpty(rsp.getStructuredInterest())){
                rsp.setStructuredInterestList(JSONUtil.toList(rsp.getStructuredInterest(), StructuredInterest.class));
            }
        }
        return rsp;
    }
}
