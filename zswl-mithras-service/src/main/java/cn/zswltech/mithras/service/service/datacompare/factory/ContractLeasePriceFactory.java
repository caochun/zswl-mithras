package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.contract.price.ContractLeasePriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractLeasePriceLibHandler;
import cn.zswltech.mithras.service.service.lib.projreview.handler.impl.ProjReviewLeasePriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("contractLeasePrice")
public class ContractLeasePriceFactory implements EditdataCompareFactory {

    @Resource
    private ContractLeasePriceLibMapper libMapper;
    @Resource
    private ContractLeasePriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ContractLeasePrice, ContractLeasePriceLib, ContractLeasePriceDetailRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.CONTRACT.name(), version);
    }
}