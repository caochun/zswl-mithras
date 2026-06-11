package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.account.ContractAccountListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAccountLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.model.contract.ContractAccountLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractAccountBLSKLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
@Service("contractAccountBLSK")
public class ContractAccountBLSKFactory implements EditdataCompareFactory {
    @Resource
    private ContractAccountLibMapper libMapper;
    @Resource
    private ContractAccountBLSKLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractAccount, ContractAccountLib, ContractAccountListRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
    }
}
