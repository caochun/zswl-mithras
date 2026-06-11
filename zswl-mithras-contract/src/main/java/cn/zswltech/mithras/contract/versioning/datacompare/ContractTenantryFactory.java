package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractTenantryLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-26
 **/
@Service("contractTenantry")
public class ContractTenantryFactory implements EditdataCompareFactory {
    @Resource
    private ContractTenantryLibMapper libMapper;
    @Resource
    private ContractTenantryLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractTenantry, ContractTenantryLib, ContractTenantryListRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
    }
}