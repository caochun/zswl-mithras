package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projpricing.price.ProjPricingLeasePriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingLeasePriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl.ProjPricingLeasePriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("projPricingLeasePrice")
public class ProjPricingLeasePriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjPricingLeasePriceLibMapper libMapper;
    @Resource
    private ProjPricingLeasePriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjPricingLeasePrice, ProjPricingLeasePriceLib, ProjPricingLeasePriceRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.PROJ_PRICING.name(), version);
    }
}