package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.rent.ContractRentActualRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentEstimateLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimateLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractRentEstimateLibHandle;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-27
 **/
@Service("contractEstimateRent")
public class ContractEstimateRentFactory implements EditdataCompareFactory {
    @Resource
    private ContractRentEstimateLibMapper libMapper;
    @Resource
    private ContractRentEstimateLibHandle handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractRentEstimate, ContractRentEstimateLib, ContractRentActualRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
    }
}