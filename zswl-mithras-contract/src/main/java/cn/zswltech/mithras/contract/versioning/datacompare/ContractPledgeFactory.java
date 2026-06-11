package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractPledgeLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledgeLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractPledgeLibHandler;
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
        return new DefaultDataCompare<ContractPledge, ContractPledgeLib, ContractPledgeListRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
    }
}