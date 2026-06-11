package cn.zswltech.mithras.projectprocess.application.lib.projestablish.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishAocPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishAocPriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.handler.ProjEstablishLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishAocPriceLibHandler
        extends ProjEstablishLibAbstractHandler<ProjEstablishAocPriceLib, ProjEstablishAocPrice, ProjEstablishAocPriceRSP> {

    @Override
    protected ProjEstablishAocPriceLib entity2Lib(ProjEstablishAocPrice f) {
        return BeanUtil.copyProperties(f, ProjEstablishAocPriceLib.class);
    }

    @Override
    protected ProjEstablishAocPrice lib2Entity(ProjEstablishAocPriceLib t) {
        return BeanUtil.copyProperties(t, ProjEstablishAocPrice.class);
    }

    @Override
    protected ProjEstablishAocPriceRSP lib2Rsp(ProjEstablishAocPriceLib f) {
        ProjEstablishAocPriceRSP rsp = BeanUtil.copyProperties(f, ProjEstablishAocPriceRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjEstablishInfoModule getSubModule() {
        return ProjEstablishInfoModule.ZR_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
