package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.afterlease.application.RentCollectionEmailBankAccountSnapshot;
import cn.zswltech.mithras.afterlease.application.RentCollectionEmailPledgeAccountPort;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RentCollectionEmailPledgeAccountPortAdapter implements RentCollectionEmailPledgeAccountPort {
    @Resource
    private cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;

    @Override
    public RentCollectionEmailBankAccountSnapshot findPledgeAccount(Long contractId) {
        List<FundFinancingPledgeInfo> contractPledgeList = fundFinancingPledgeInfoService.findContractPledgeList(contractId);
        if (ObjectUtil.isNotEmpty(contractPledgeList)) {
            FundFinancingPledgeInfo pledgeInfo = contractPledgeList.get(0);
            return toSnapshot(pledgeInfo.getAccountBank(), pledgeInfo.getAccountName(), pledgeInfo.getAccountNumber());
        }
        List<FundDirectFinancingPledgeInfo> directPledge = fundDirectFinancingPledgeInfoService.findContractPledgeList(contractId);
        if (ObjectUtil.isNotEmpty(directPledge)) {
            FundDirectFinancingPledgeInfo pledgeInfo = directPledge.get(0);
            return toSnapshot(pledgeInfo.getAccountBank(), pledgeInfo.getAccountName(), pledgeInfo.getAccountNumber());
        }
        return RentCollectionEmailBankAccountSnapshot.builder().build();
    }

    private RentCollectionEmailBankAccountSnapshot toSnapshot(String accountBank, String accountName, String accountNumber) {
        return RentCollectionEmailBankAccountSnapshot.builder()
                .accountBank(accountBank)
                .accountName(accountName)
                .accountNumber(accountNumber)
                .build();
    }
}
