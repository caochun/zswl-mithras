package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractLeaseItemListRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageItemListRSP;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeaseItemLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageItem;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageItemLib;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractMortgageItemLibHandler
        extends ContractLibAbstractHandler<ContractMortgageItemLib, ContractMortgageItem, ContractMortgageItemListRSP> {

    @Override
    protected ContractMortgageItemLib entity2Lib(ContractMortgageItem f) {
        return BeanUtil.copyProperties(f, ContractMortgageItemLib.class);
    }

    @Override
    protected ContractMortgageItem lib2Entity(ContractMortgageItemLib t) {
        return BeanUtil.copyProperties(t, ContractMortgageItem.class);
    }

    @Override
    protected ContractMortgageItemListRSP lib2Rsp(ContractMortgageItemLib f) {
        ContractMortgageItemListRSP rsp = BeanUtil.copyProperties(f, ContractMortgageItemListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.MORTGAGE_ITEM;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
