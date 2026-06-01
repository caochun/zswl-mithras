package cn.zswltech.mithras.service.service.lib.projestablish.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishLeasePriceLib;
import cn.zswltech.mithras.service.service.lib.projestablish.handler.ProjEstablishLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishLeasePriceLibHandler
        extends ProjEstablishLibAbstractHandler<ProjEstablishLeasePriceLib, ProjEstablishLeasePrice, ProjEstablishLeasePriceRSP> {

    @Override
    protected ProjEstablishLeasePriceLib entity2Lib(ProjEstablishLeasePrice f) {
        return BeanUtil.copyProperties(f, ProjEstablishLeasePriceLib.class);
    }

    @Override
    protected ProjEstablishLeasePrice lib2Entity(ProjEstablishLeasePriceLib t) {
        return BeanUtil.copyProperties(t, ProjEstablishLeasePrice.class);
    }

    @Override
    protected ProjEstablishLeasePriceRSP lib2Rsp(ProjEstablishLeasePriceLib f) {
        ProjEstablishLeasePriceRSP rsp = BeanUtil.copyProperties(f, ProjEstablishLeasePriceRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjEstablishInfoModule getSubModule() {
        return ProjEstablishInfoModule.ZL_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
