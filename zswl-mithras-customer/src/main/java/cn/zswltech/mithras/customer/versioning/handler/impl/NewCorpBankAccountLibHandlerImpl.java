package cn.zswltech.mithras.customer.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.bankaccount.NewCorpBankAccountListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.model.client.NewCorpBankAccount;
import cn.zswltech.mithras.customer.model.client.NewCorpBankAccountLib;
import cn.zswltech.mithras.customer.versioning.handler.ClientLibAbstractHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Component
public class NewCorpBankAccountLibHandlerImpl extends ClientLibAbstractHandler<NewCorpBankAccountLib, NewCorpBankAccount, NewCorpBankAccountListRSP> {

    @Override
    protected NewCorpBankAccountLib entity2Lib(NewCorpBankAccount newCorpBankAccount) {
        NewCorpBankAccountLib newCorpBankAccountLib = BeanUtil.copyProperties(newCorpBankAccount, NewCorpBankAccountLib.class);
        return newCorpBankAccountLib;
    }

    @Override
    protected NewCorpBankAccount lib2Entity(NewCorpBankAccountLib newCorpBankAccountLib) {
        NewCorpBankAccount newCorpBankAccount = BeanUtil.copyProperties(newCorpBankAccountLib, NewCorpBankAccount.class);
        return newCorpBankAccount;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.CORP_BANK_ACCOUNT;
    }

    @Override
    protected NewCorpBankAccountListRSP lib2Rsp(NewCorpBankAccountLib f) {
        NewCorpBankAccountListRSP rsp = BeanUtil.copyProperties(f, NewCorpBankAccountListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public List<NewCorpBankAccount> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
