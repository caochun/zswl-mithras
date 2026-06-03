package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.contract.price.ContractAocPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAocPriceLibMapper;
import cn.zswltech.mithras.service.mapper.lib.projreview.ProjReviewAocPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPriceLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractAocPriceLibHandler;
import cn.zswltech.mithras.service.service.lib.projreview.handler.impl.ProjReviewAocPriceLibHandler;
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
        return new DefaultDataCompare<ContractAocPrice, ContractAocPriceLib, ContractAocPriceDetailRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.CONTRACT.name(), version);
    }
}