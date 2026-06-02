package cn.zswltech.mithras.contract.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeItemListRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledgeItem;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledgeItemLib;
import cn.zswltech.mithras.contract.service.lib.contract.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2022/10/10
 * @description
 */
@Service
public class ContractPledgeItemLibHandler extends ContractLibAbstractHandler<ContractPledgeItemLib, ContractPledgeItem, ContractPledgeItemListRSP> {

    @Override
    protected ContractPledgeItemLib entity2Lib(ContractPledgeItem f) {
        return BeanUtil.copyProperties(f, ContractPledgeItemLib.class);
    }

    @Override
    protected ContractPledgeItem lib2Entity(ContractPledgeItemLib t) {
        return BeanUtil.copyProperties(t, ContractPledgeItem.class);
    }

    @Override
    protected ContractPledgeItemListRSP lib2Rsp(ContractPledgeItemLib f) {
        ContractPledgeItemListRSP rsp = BeanUtil.copyProperties(f, ContractPledgeItemListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.PLEDGE_ITEM;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
