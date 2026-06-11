package cn.zswltech.mithras.customer.datacompare;

import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpBankAccountLibMapper;
import cn.zswltech.mithras.customer.model.client.CorpBankAccount;
import cn.zswltech.mithras.customer.model.client.CorpBankAccountLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.versioning.handler.impl.CorpBankAccountLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("corpBankAccount")
public class CorpBankAccountFactory implements EditdataCompareFactory {

    @Resource
    private CorpBankAccountLibMapper libMapper;
    @Resource
    private CorpBankAccountLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<CorpBankAccount, CorpBankAccountLib, CorpBankAccountListRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}