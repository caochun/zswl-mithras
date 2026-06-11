package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.model.client.NormalBankAccount;
import cn.zswltech.mithras.customer.model.client.NormalBankAccountLib;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NormalBankAccountLibHandlerImpl extends ClientLibAbstractHandler<NormalBankAccountLib, NormalBankAccount, NormalBankAccountListRSP> {

    @Override
    protected NormalBankAccountLib entity2Lib(NormalBankAccount normalBankAccount) {
        NormalBankAccountLib normalBankAccountLib = BeanUtil.copyProperties(normalBankAccount, NormalBankAccountLib.class);
        return normalBankAccountLib;
    }

    @Override
    protected NormalBankAccount lib2Entity(NormalBankAccountLib normalBankAccountLib) {
        NormalBankAccount normalBankAccount = BeanUtil.copyProperties(normalBankAccountLib, NormalBankAccount.class);
        return normalBankAccount;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.NORMAL.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.NORMAL_BANK_ACCOUNT;
    }

    @Override
    protected NormalBankAccountListRSP lib2Rsp(NormalBankAccountLib f) {
        NormalBankAccountListRSP rsp = BeanUtil.copyProperties(f, NormalBankAccountListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public List<NormalBankAccount> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
