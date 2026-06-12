package cn.zswltech.mithras.application.orchestration.adapter.basedata;

import cn.zswltech.mithras.basedata.application.bankaccount.BaseDataBankAccountSyncPort;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.core.ContractAccountService;
import cn.zswltech.mithras.contract.model.contract.ContractAccount;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundRepayAccount;
import cn.zswltech.mithras.margin.model.MarginRecordInfo;
import cn.zswltech.mithras.margin.service.MarginRecordService;
import cn.zswltech.mithras.payment.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundRepayAccountService;
import cn.zswltech.mithras.application.orchestration.liquiditymanage.FundFinancingAccountSettingService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailUnconfirmedService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class BaseDataBankAccountSyncPortAdapter implements BaseDataBankAccountSyncPort {

    @Resource
    private FundDirectFinancingPledgeInfoService directFinancingPledgeInfoService;
    @Resource
    private FundFinancingAccountSettingService accountSettingService;
    @Resource
    private FundFinancingPayAccountService fundFinancingPayAccountService;
    @Resource
    private FundRepayAccountService fundRepayAccountService;
    @Resource
    private PaymentActualDetailUnconfirmedService actualDetailUnconfirmedService;
    @Resource
    private ContractAccountService contractAccountService;
    @Resource
    private cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private MarginRecordService marginRecordService;

    @Override
    public void syncBankAccount(BaseDataBankAccount baseDataBankAccount) {
        String accountName = baseDataBankAccount.getAccountName();
        String accountNumber = baseDataBankAccount.getAccountNumber();
        String accountBank = baseDataBankAccount.getAccountBank();
        List<String> accountNumberList = new ArrayList<>();
        accountNumberList.add(accountNumber);
        accountNumberList.add(formatNumber(accountNumber));

        directFinancingPledgeInfoService.update(Wrappers.<FundDirectFinancingPledgeInfo>lambdaUpdate()
                .in(FundDirectFinancingPledgeInfo::getAccountNumber, accountNumberList)
                .set(FundDirectFinancingPledgeInfo::getAccountName, accountName)
                .set(FundDirectFinancingPledgeInfo::getAccountBank, accountBank));
        accountSettingService.update(Wrappers.<cn.zswltech.mithras.liquidity.model.FundFinancingAccountSetting>lambdaUpdate()
                .in(cn.zswltech.mithras.liquidity.model.FundFinancingAccountSetting::getAccountNumber, accountNumberList)
                .set(cn.zswltech.mithras.liquidity.model.FundFinancingAccountSetting::getAccountBank, accountBank));
        fundFinancingPayAccountService.update(Wrappers.<FundFinancingPayAccount>lambdaUpdate()
                .in(FundFinancingPayAccount::getAccountNumber, accountNumberList)
                .set(FundFinancingPayAccount::getAccountBank, accountBank));
        fundRepayAccountService.update(Wrappers.<FundRepayAccount>lambdaUpdate()
                .in(FundRepayAccount::getAccountNumber, accountNumberList)
                .set(FundRepayAccount::getAccountBank, accountBank));
        actualDetailUnconfirmedService.update(Wrappers.<PaymentActualDetailUnconfirmed>lambdaUpdate()
                .in(PaymentActualDetailUnconfirmed::getOurAccountNumber, accountNumberList)
                .set(PaymentActualDetailUnconfirmed::getOurAccountName, accountName)
                .set(PaymentActualDetailUnconfirmed::getOurAccountBank, accountBank));
        contractAccountService.update(Wrappers.<ContractAccount>lambdaUpdate()
                .in(ContractAccount::getAccountNum, accountNumberList)
                .set(ContractAccount::getAccountName, accountName)
                .set(ContractAccount::getAccountAddress, accountBank));
        fundFinancingPledgeInfoService.update(Wrappers.<FundFinancingPledgeInfo>lambdaUpdate()
                .in(FundFinancingPledgeInfo::getAccountNumber, accountNumberList)
                .set(FundFinancingPledgeInfo::getAccountName, accountName)
                .set(FundFinancingPledgeInfo::getAccountBank, accountBank));
        collectionRecordInfoService.update(Wrappers.<CollectionRecordInfo>lambdaUpdate()
                .in(CollectionRecordInfo::getOurAccountNumber, accountNumberList)
                .set(CollectionRecordInfo::getOurAccountName, accountName)
                .set(CollectionRecordInfo::getOurAccountBank, accountBank));
        marginRecordService.update(Wrappers.<MarginRecordInfo>lambdaUpdate()
                .in(MarginRecordInfo::getOurAccountNumber, accountNumberList)
                .set(MarginRecordInfo::getOurAccountName, accountName)
                .set(MarginRecordInfo::getOurAccountBank, accountBank));
    }

    private static String formatNumber(String number) {
        StringBuilder formatted = new StringBuilder();
        int length = number.length();
        for (int i = 0; i < length; i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(number.charAt(i));
        }
        return formatted.toString();
    }
}
