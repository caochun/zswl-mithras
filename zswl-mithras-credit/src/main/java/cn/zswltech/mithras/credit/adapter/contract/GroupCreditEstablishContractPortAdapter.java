package cn.zswltech.mithras.credit.adapter.contract;

import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishContractPort;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class GroupCreditEstablishContractPortAdapter implements GroupCreditEstablishContractPort {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public Long getGroupCreditStockRiskExposure(Long clientId) {
        return contractBaseInfoService.getGroupCreditStockRiskExposure(clientId);
    }
}
