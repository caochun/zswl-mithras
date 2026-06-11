package cn.zswltech.mithras.contract.versioning.datacompare;

import cn.zswltech.mithras.dto.contract.price.ContractAocPriceDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAocPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPriceLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractAocPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("contractAocPrice")
public class ContractAocPriceFactory implements EditdataCompareFactory {

    @Resource
    private ContractAocPriceLibMapper libMapper;
    @Resource
    private ContractAocPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractAocPrice, ContractAocPriceLib, ContractAocPriceDetailRSP>(rsps, libMapper, handler, commonVersionMapper,"CONTRACT", version);
    }
}