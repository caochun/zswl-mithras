package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projpricing.price.ProjPricingAocPriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingAocPriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl.ProjPricingAocPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("projPricingAocPrice")
public class ProjPricingAocPriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjPricingAocPriceLibMapper libMapper;
    @Resource
    private ProjPricingAocPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjPricingAocPrice, ProjPricingAocPriceLib, ProjPricingAocPriceRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.PROJ_PRICING.name(), version);
    }
}