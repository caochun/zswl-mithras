package cn.zswltech.mithras.service.service.lib.projpricing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingAocPriceRSP;
import cn.zswltech.mithras.service.enums.projpricing.ProjPricingInfoModule;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingAocPriceLib;
import cn.zswltech.mithras.service.service.lib.projpricing.handler.ProjPricingLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjPricingAocPriceLibHandler
        extends ProjPricingLibAbstractHandler<ProjPricingAocPriceLib, ProjPricingAocPrice, ProjPricingAocPriceRSP> {

    @Override
    protected ProjPricingAocPriceLib entity2Lib(ProjPricingAocPrice f) {
        return BeanUtil.copyProperties(f, ProjPricingAocPriceLib.class);
    }

    @Override
    protected ProjPricingAocPrice lib2Entity(ProjPricingAocPriceLib t) {
        return BeanUtil.copyProperties(t, ProjPricingAocPrice.class);
    }

    @Override
    protected ProjPricingAocPriceRSP lib2Rsp(ProjPricingAocPriceLib f) {
        ProjPricingAocPriceRSP rsp = BeanUtil.copyProperties(f, ProjPricingAocPriceRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjPricingInfoModule getSubModule() {
        return ProjPricingInfoModule.ZR_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
