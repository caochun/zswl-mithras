package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.leaseitem.ContractLeaseItemListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.model.contract.ContractLeaseItemLib;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractLeaseItemLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractLeaseItemLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-26
 **/
@Service("contractLeaseItem")
public class ContractLeaseItemFactory implements EditdataCompareFactory {
    @Resource
    private ContractLeaseItemLibMapper libMapper;
    @Resource
    private ContractLeaseItemLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractLeaseItem, ContractLeaseItemLib, ContractLeaseItemListRSP.RowDataModel>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
    }
}