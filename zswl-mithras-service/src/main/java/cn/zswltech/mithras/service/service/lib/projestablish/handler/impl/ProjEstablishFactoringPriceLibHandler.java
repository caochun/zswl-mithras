package cn.zswltech.mithras.service.service.lib.projestablish.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishFactoringPriceLib;
import cn.zswltech.mithras.service.service.lib.projestablish.handler.ProjEstablishLibAbstractHandler;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishPriceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishFactoringPriceLibHandler
        extends ProjEstablishLibAbstractHandler<ProjEstablishFactoringPriceLib, ProjEstablishFactoringPrice, ProjEstablishFactoringPriceRSP> {
    @Resource
    private ProjEstablishPriceService priceService;

    @Override
    protected ProjEstablishFactoringPriceLib entity2Lib(ProjEstablishFactoringPrice f) {
        return BeanUtil.copyProperties(f, ProjEstablishFactoringPriceLib.class);
    }

    @Override
    protected ProjEstablishFactoringPrice lib2Entity(ProjEstablishFactoringPriceLib t) {
        return BeanUtil.copyProperties(t, ProjEstablishFactoringPrice.class);
    }

    @Override
    protected ProjEstablishFactoringPriceRSP lib2Rsp(ProjEstablishFactoringPriceLib f) {
        ProjEstablishFactoringPriceRSP rsp = BeanUtil.copyProperties(f, ProjEstablishFactoringPriceRSP.class);
        rsp.setContracts(priceService.getSurvivingContract(rsp.getProjEstablishId()));
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjEstablishInfoModule getSubModule() {
        return ProjEstablishInfoModule.BL_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
