package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.contract.rent.ContractRentActualRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractRentEstimateLibMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.service.mapper.model.contract.ContractRentEstimateLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractRentEstimateLibHandle;
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
        return new DefaultDataCompare<ContractRentEstimate, ContractRentEstimateLib, ContractRentActualRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.CONTRACT.name(), version);
    }
}