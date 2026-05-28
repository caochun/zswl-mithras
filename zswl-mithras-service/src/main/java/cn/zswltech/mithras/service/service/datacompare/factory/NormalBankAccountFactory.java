package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.NormalBankAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.NormalBankAccount;
import cn.zswltech.mithras.service.mapper.model.client.NormalBankAccountLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.NormalBankAccountLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("normalBankAccount")
public class NormalBankAccountFactory implements EditdataCompareFactory {

    @Resource
    private NormalBankAccountLibMapper libMapper;
    @Resource
    private NormalBankAccountLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NormalBankAccount, NormalBankAccountLib, NormalBankAccountListRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.CLIENT.name(), version);
    }
}
