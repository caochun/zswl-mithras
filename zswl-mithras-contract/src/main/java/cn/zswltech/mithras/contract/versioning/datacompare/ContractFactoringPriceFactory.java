package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.price.ContractFactoringPriceDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractFactoringPriceLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.model.contract.ContractFactoringPriceLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractFactoringPriceLibHandler;
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
        return new DefaultDataCompare<ContractFactoringPrice, ContractFactoringPriceLib, ContractFactoringPriceDetailRSP>(rsps,libMapper,handler, commonVersionMapper,"CONTRACT", version);
    }
}
