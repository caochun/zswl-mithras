package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projpricing.price.ProjPricingFactoringPriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.projpricing.ProjPricingFactoringPriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingFactoringPriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.projpricing.handler.impl.ProjPricingFactoringPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-03
 **/
@Service("projPricingFactoringPrice")
public class ProjPricingFactoringPriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjPricingFactoringPriceLibMapper libMapper;
    @Resource
    private ProjPricingFactoringPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjPricingFactoringPrice, ProjPricingFactoringPriceLib, ProjPricingFactoringPriceRSP>(rsps,libMapper,handler, commonVersionMapper,BusinessModuleEnum.PROJ_PRICING.name(), version);
    }
}
