package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.contract.price.ContractFactoringPriceDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractFactoringPriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractFactoringPriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractFactoringPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-03
 **/
@Service("contractFactoringPrice")
public class ContractFactoringPriceFactory implements EditdataCompareFactory {
    @Resource
    private ContractFactoringPriceLibMapper libMapper;
    @Resource
    private ContractFactoringPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractFactoringPrice, ContractFactoringPriceLib, ContractFactoringPriceDetailRSP>(rsps,libMapper,handler, commonVersionMapper,BusinessModuleEnum.CONTRACT.name(), version);
    }
}
