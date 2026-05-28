package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.contract.price.ContractFactoringPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.StructuredInterest;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractFactoringPriceLib;
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
public class ContractFactoringPriceLibHandler extends ContractLibAbstractHandler<ContractFactoringPriceLib, ContractFactoringPrice, ContractFactoringPriceDetailRSP> {
    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.BL_PRICE;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected ContractFactoringPriceLib entity2Lib(ContractFactoringPrice f) {
        return BeanUtil.copyProperties(f, ContractFactoringPriceLib.class);
    }

    @Override
    protected ContractFactoringPrice lib2Entity(ContractFactoringPriceLib t) {
        return BeanUtil.copyProperties(t, ContractFactoringPrice.class);
    }

    @Override
    protected ContractFactoringPriceDetailRSP lib2Rsp(ContractFactoringPriceLib f) {
        ContractFactoringPriceDetailRSP rsp = BeanUtil.copyProperties(f, ContractFactoringPriceDetailRSP.class);
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
