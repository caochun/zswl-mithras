package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.RentCollectionEmailBankAccountPort;
import cn.zswltech.mithras.afterlease.application.RentCollectionEmailBankAccountSnapshot;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;
import cn.zswltech.mithras.basedata.service.BaseDataBankAccountService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RentCollectionEmailBankAccountPortAdapter implements RentCollectionEmailBankAccountPort {
    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;

    @Override
    public RentCollectionEmailBankAccountSnapshot getById(Long bankId) {
        return toSnapshot(baseDataBankAccountService.getById(bankId));
    }

    @Override
    public RentCollectionEmailBankAccountSnapshot findByAccount(String accountBank, String accountName, String accountNumber) {
        BaseDataBankAccount bankAccount = baseDataBankAccountService.getOne(Wrappers.<BaseDataBankAccount>lambdaQuery()
                .eq(BaseDataBankAccount::getAccountBank, accountBank)
                .eq(BaseDataBankAccount::getAccountName, accountName)
                .and(i -> i.or().eq(BaseDataBankAccount::getAccountNumber, accountNumber)
                        .or().eq(BaseDataBankAccount::getAccountNumber, StringUtils.replace(accountNumber, " ", ""))));
        return toSnapshot(bankAccount);
    }

    private RentCollectionEmailBankAccountSnapshot toSnapshot(BaseDataBankAccount bankAccount) {
        if (bankAccount == null) {
            return null;
        }
        return RentCollectionEmailBankAccountSnapshot.builder()
                .id(bankAccount.getId())
                .accountBank(bankAccount.getAccountBank())
                .accountName(bankAccount.getAccountName())
                .accountNumber(bankAccount.getAccountNumber())
                .build();
    }
}
