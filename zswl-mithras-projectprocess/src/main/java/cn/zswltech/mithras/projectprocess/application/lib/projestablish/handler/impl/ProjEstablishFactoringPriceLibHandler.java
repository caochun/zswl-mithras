package cn.zswltech.mithras.projectprocess.application.lib.projestablish.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessSurvivingContractResolver;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.handler.ProjEstablishLibAbstractHandler;
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
    private ProjectProcessSurvivingContractResolver survivingContractResolver;

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
        rsp.setContracts(survivingContractResolver.resolveProjEstablishSurvivingContracts(rsp.getProjEstablishId()));
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
