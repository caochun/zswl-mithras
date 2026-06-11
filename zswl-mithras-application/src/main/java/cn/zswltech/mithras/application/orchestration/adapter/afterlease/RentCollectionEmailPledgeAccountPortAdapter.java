package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.afterlease.application.RentCollectionEmailPledgeAccountPort;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListRSP;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.direct.mapper.model.FundDirectFinancingPledgeInfo;
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
    public BaseDataBankAccountListRSP findPledgeAccount(Long contractId) {
        BaseDataBankAccountListRSP account = new BaseDataBankAccountListRSP();
        List<FundFinancingPledgeInfo> contractPledgeList = fundFinancingPledgeInfoService.findContractPledgeList(contractId);
        if (ObjectUtil.isNotEmpty(contractPledgeList)) {
            FundFinancingPledgeInfo pledgeInfo = contractPledgeList.get(0);
            account.setAccountBank(pledgeInfo.getAccountBank());
            account.setAccountName(pledgeInfo.getAccountName());
            account.setAccountNumber(pledgeInfo.getAccountNumber());
            return account;
        }
        List<FundDirectFinancingPledgeInfo> directPledge = fundDirectFinancingPledgeInfoService.findContractPledgeList(contractId);
        if (ObjectUtil.isNotEmpty(directPledge)) {
            FundDirectFinancingPledgeInfo pledgeInfo = directPledge.get(0);
            account.setAccountBank(pledgeInfo.getAccountBank());
            account.setAccountName(pledgeInfo.getAccountName());
            account.setAccountNumber(pledgeInfo.getAccountNumber());
        }
        return account;
    }
}
