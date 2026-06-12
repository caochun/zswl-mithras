package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.model.client.CorpBankAccount;
import cn.zswltech.mithras.customer.model.client.NewCorpBankAccount;
import cn.zswltech.mithras.customer.application.client.model.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.CorpBankAccountService;
import cn.zswltech.mithras.customer.application.client.NewCorpBankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
@Slf4j
@Component
public class CorpBankAccountCopyHandler extends AbstractClientDataCopyHandler<CorpBankAccount, NewCorpBankAccount, CorpBankAccountService, NewCorpBankAccountService> {
    @Resource
    private CorpBankAccountService oldCorpBankAccountService;
    @Resource
    private NewCorpBankAccountService newCorpBankAccountService;

    @Override
    protected CorpBankAccountService getOldService() {
        return oldCorpBankAccountService;
    }

    @Override
    protected NewCorpBankAccountService getNewService() {
        return newCorpBankAccountService;
    }

    @Override
    protected NewCorpBankAccount copyNewModel(CorpBankAccount corpBankAccount, ClientCopyInfoBO clientCopyInfoBO) {
        NewCorpBankAccount newCorpBankAccount = this.copyIgnoreBaseField(corpBankAccount, NewCorpBankAccount.class);
        newCorpBankAccount.setUserId(clientCopyInfoBO.getCurrentUserId());
        return newCorpBankAccount;
    }

    @Override
    protected CorpBankAccount copyOldModel(NewCorpBankAccount newCorpBankAccount, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyIgnoreBaseField(newCorpBankAccount, CorpBankAccount.class);
    }

    @Override
    protected NewCorpBankAccount copyNewModelFromNewModel(NewCorpBankAccount otherNewCorpBankAccount, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyNewModel(otherNewCorpBankAccount, clientCopyInfoBO);
    }

    @Override
    public InfoModule getInfoModule() {
        return InfoModule.CORP_BANK_ACCOUNT;
    }
}
