package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpBankAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpBankAccount;
import cn.zswltech.mithras.service.mapper.model.client.CorpBankAccountLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpBankAccountLibHandlerImpl;
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
        return new DefaultDataCompare<CorpBankAccount, CorpBankAccountLib, CorpBankAccountListRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.CLIENT.name(), version);
    }
}