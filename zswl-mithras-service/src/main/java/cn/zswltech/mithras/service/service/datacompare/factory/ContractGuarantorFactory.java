package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractGuarantorLibMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.service.mapper.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractGuarantorLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-26
 **/
@Service("contractGuarantor")
public class ContractGuarantorFactory implements EditdataCompareFactory {
    @Resource
    private ContractGuarantorLibMapper libMapper;
    @Resource
    private ContractGuarantorLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractGuarantor, ContractGuarantorLib, ContractGuarantorListRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.CONTRACT.name(), version);
    }
}