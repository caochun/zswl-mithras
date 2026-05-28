package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractPledgeLibMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledgeLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractPledgeLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-26
 **/
@Service("contractPledge")
public class ContractPledgeFactory implements EditdataCompareFactory {
    @Resource
    private ContractPledgeLibMapper libMapper;
    @Resource
    private ContractPledgeLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractPledge, ContractPledgeLib, ContractPledgeListRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.CONTRACT.name(), version);
    }
}