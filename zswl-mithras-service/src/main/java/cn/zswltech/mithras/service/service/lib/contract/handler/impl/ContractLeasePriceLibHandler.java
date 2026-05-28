package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.contract.price.ContractLeasePriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.StructuredInterest;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.service.util.FinancialUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractLeasePriceLibHandler
        extends ContractLibAbstractHandler<ContractLeasePriceLib, ContractLeasePrice, ContractLeasePriceDetailRSP> {

    @Override
    protected ContractLeasePriceLib entity2Lib(ContractLeasePrice f) {
        return BeanUtil.copyProperties(f, ContractLeasePriceLib.class);
    }

    @Override
    protected ContractLeasePrice lib2Entity(ContractLeasePriceLib t) {
        return BeanUtil.copyProperties(t, ContractLeasePrice.class);
    }

    @Override
    protected ContractLeasePriceDetailRSP lib2Rsp(ContractLeasePriceLib f) {
        ContractLeasePriceDetailRSP rsp = BeanUtil.copyProperties(f, ContractLeasePriceDetailRSP.class);
        if (Objects.nonNull(rsp)) {
            rsp.setId(f.getOriginId());
            if (Objects.nonNull(rsp.getApplyCreditAmount()) && Objects.nonNull(rsp.getEarnestMoney())) {
                rsp.setEarnestMoneyRate(FinancialUtil.calculateFeeRate(rsp.getEarnestMoney(), rsp.getApplyCreditAmount()));
            }
            if (Objects.nonNull(rsp.getApplyCreditAmount()) && Objects.nonNull(rsp.getConsultingFee())) {
                rsp.setConsultingFeeRate(FinancialUtil.calculateFeeRate(rsp.getConsultingFee(), rsp.getApplyCreditAmount()));
            }
            if(ObjectUtil.isNotEmpty(rsp.getStructuredInterest())){
                rsp.setStructuredInterestList(JSONUtil.toList(rsp.getStructuredInterest(), StructuredInterest.class));
            }
        }
        return rsp;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.ZL_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
