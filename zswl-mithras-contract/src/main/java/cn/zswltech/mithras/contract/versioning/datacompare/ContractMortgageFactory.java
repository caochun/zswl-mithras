package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractMortgageLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.model.contract.ContractMortgageLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractMortgageLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-26
 **/
@Service("contractMortgage")
public class ContractMortgageFactory implements EditdataCompareFactory {
    @Resource
    private ContractMortgageLibMapper libMapper;
    @Resource
    private ContractMortgageLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractMortgage, ContractMortgageLib, ContractMortgageListRSP>(rsps, libMapper, handler,commonVersionMapper, "CONTRACT", version);
    }
}