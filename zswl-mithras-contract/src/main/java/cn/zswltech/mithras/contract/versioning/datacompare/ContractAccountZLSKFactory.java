package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.account.ContractAccountListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAccountLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccountLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractAccountZLSKLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-26
 **/
@Service("contractAccountZLSK")
public class ContractAccountZLSKFactory implements EditdataCompareFactory {
    @Resource
    private ContractAccountLibMapper libMapper;
    @Resource
    private ContractAccountZLSKLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractAccount, ContractAccountLib, ContractAccountListRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
    }
}