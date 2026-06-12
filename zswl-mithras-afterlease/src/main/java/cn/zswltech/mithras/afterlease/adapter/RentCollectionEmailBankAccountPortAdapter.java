package cn.zswltech.mithras.afterlease.adapter;

import cn.zswltech.mithras.afterlease.application.RentCollectionEmailBankAccountPort;
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
    public BaseDataBankAccount getById(Long bankId) {
        return baseDataBankAccountService.getById(bankId);
    }

    @Override
    public BaseDataBankAccount findByAccount(String accountBank, String accountName, String accountNumber) {
        return baseDataBankAccountService.getOne(Wrappers.<BaseDataBankAccount>lambdaQuery()
                .eq(BaseDataBankAccount::getAccountBank, accountBank)
                .eq(BaseDataBankAccount::getAccountName, accountName)
                .and(i -> i.or().eq(BaseDataBankAccount::getAccountNumber, accountNumber)
                        .or().eq(BaseDataBankAccount::getAccountNumber, StringUtils.replace(accountNumber, " ", ""))));
    }
}
