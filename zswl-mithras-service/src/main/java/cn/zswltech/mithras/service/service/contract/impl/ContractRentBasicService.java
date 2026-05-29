package cn.zswltech.mithras.service.service.contract.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.contract.price.*;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.bo.ContractPriceHelperBO;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2022/8/23
 * @description
 */
@Component
public class ContractRentBasicService {
    @Resource
    private ContractPriceService contractPriceService;

    public ContractPriceHelperBO getContractPriceItemByPlan(ContractBaseInfo contractBaseInfo) {
        ContractPriceDetailREQ contractPriceDetailREQ = new ContractPriceDetailREQ();
        contractPriceDetailREQ.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.detail(contractPriceDetailREQ);
        ContractPriceHelperBO contractPriceHelperBO = new ContractPriceHelperBO();
        contractPriceHelperBO.setFirstDate(contractBaseInfo.getEstimatedLeaseDate());
        ProjectBizType projectBizType = ProjectBizType.of(contractBaseInfo.getBizType());
        contractPriceHelperBO.setProjectBizType(projectBizType);
        switch (projectBizType) {
            case ZL:
            case ZZ: {
                ContractLeasePriceDetailRSP contractLeasePriceDetailRSP = Assert.notNull(contractPriceDetailRSP.getLeasePriceModifyRSP(), () -> MithrasException.newException("请先维护报价方案"));
                contractPriceHelperBO.setContractAmount(contractLeasePriceDetailRSP.getApplyCreditAmount());
                contractPriceHelperBO.setConsultingFee(Optional.ofNullable(contractLeasePriceDetailRSP.getConsultingFee()).orElse(0L));
                contractPriceHelperBO.setDownPayment(Optional.ofNullable(contractLeasePriceDetailRSP.getDownPayment()).orElse(0L));
                contractPriceHelperBO.setEarnestMoney(Optional.ofNullable(contractLeasePriceDetailRSP.getEarnestMoney()).orElse(0L));
                contractPriceHelperBO.setNominalPrice(Optional.ofNullable(contractLeasePriceDetailRSP.getNominalPrice()).orElse(0L));
                break;
            }
            case BL: {
                ContractFactoringPriceDetailRSP contractFactoringPriceDetailRSP = Assert.notNull(contractPriceDetailRSP.getFactoringPriceRSP(), () -> MithrasException.newException("请先维护报价方案"));
                contractPriceHelperBO.setContractAmount(contractFactoringPriceDetailRSP.getContractAmount());
                contractPriceHelperBO.setConsultingFee(Optional.ofNullable(contractFactoringPriceDetailRSP.getConsultingFee()).orElse(0L));
                contractPriceHelperBO.setEarnestMoney(Optional.ofNullable(contractFactoringPriceDetailRSP.getEarnestMoney()).orElse(0L));
                contractPriceHelperBO.setDownPayment(0L);
                break;
            }
            case ZR: {
                ContractAocPriceDetailRSP contractAocPriceDetailRSP = Assert.notNull(contractPriceDetailRSP.getAocPriceRSP(), () -> MithrasException.newException("请先维护报价方案"));
                contractPriceHelperBO.setContractAmount(contractAocPriceDetailRSP.getContractAmount());
                contractPriceHelperBO.setConsultingFee(Optional.ofNullable(contractAocPriceDetailRSP.getConsultingFee()).orElse(0L));
                contractPriceHelperBO.setEarnestMoney(Optional.ofNullable(contractAocPriceDetailRSP.getEarnestMoney()).orElse(0L));
                contractPriceHelperBO.setDownPayment(0L);
                break;
            }
        }
        return contractPriceHelperBO;
    }
}
