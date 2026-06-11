package cn.zswltech.mithras.customer.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.model.client.CorpBankAccount;
import cn.zswltech.mithras.customer.model.client.CorpBankAccountLib;
import cn.zswltech.mithras.customer.versioning.handler.ClientLibAbstractHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Component
public class CorpBankAccountLibHandlerImpl extends ClientLibAbstractHandler<CorpBankAccountLib, CorpBankAccount, CorpBankAccountListRSP> {

    @Override
    protected CorpBankAccountLib entity2Lib(CorpBankAccount corpBankAccount) {
        CorpBankAccountLib corpBankAccountLib = BeanUtil.copyProperties(corpBankAccount, CorpBankAccountLib.class);
        return corpBankAccountLib;
    }

    @Override
    protected CorpBankAccount lib2Entity(CorpBankAccountLib corpBankAccountLib) {
        CorpBankAccount corpBankAccount = BeanUtil.copyProperties(corpBankAccountLib, CorpBankAccount.class);
        return corpBankAccount;
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
    protected CorpBankAccountListRSP lib2Rsp(CorpBankAccountLib f) {
        CorpBankAccountListRSP rsp = BeanUtil.copyProperties(f, CorpBankAccountListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public List<CorpBankAccount> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
