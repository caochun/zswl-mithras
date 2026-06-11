package cn.zswltech.mithras.customer.datacompare;

import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.NormalBankAccountLibMapper;
import cn.zswltech.mithras.customer.model.client.NormalBankAccount;
import cn.zswltech.mithras.customer.model.client.NormalBankAccountLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.versioning.handler.impl.NormalBankAccountLibHandlerImpl;
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
        return new DefaultDataCompare<NormalBankAccount, NormalBankAccountLib, NormalBankAccountListRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}
