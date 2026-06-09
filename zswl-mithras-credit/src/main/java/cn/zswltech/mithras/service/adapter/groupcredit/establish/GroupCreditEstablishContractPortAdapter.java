package cn.zswltech.mithras.service.adapter.groupcredit.establish;

import cn.zswltech.mithras.credit.application.groupcredit.establish.service.GroupCreditEstablishContractPort;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
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
