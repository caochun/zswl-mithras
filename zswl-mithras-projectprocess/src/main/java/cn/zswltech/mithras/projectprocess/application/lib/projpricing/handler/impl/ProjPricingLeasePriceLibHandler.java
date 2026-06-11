package cn.zswltech.mithras.projectprocess.application.lib.projpricing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingLeasePriceRSP;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingInfoModule;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projpricing.handler.ProjPricingLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjPricingLeasePriceLibHandler
        extends ProjPricingLibAbstractHandler<ProjPricingLeasePriceLib, ProjPricingLeasePrice, ProjPricingLeasePriceRSP> {

    @Override
    protected ProjPricingLeasePriceLib entity2Lib(ProjPricingLeasePrice f) {
        return BeanUtil.copyProperties(f, ProjPricingLeasePriceLib.class);
    }

    @Override
    protected ProjPricingLeasePrice lib2Entity(ProjPricingLeasePriceLib t) {
        return BeanUtil.copyProperties(t, ProjPricingLeasePrice.class);
    }

    @Override
    protected ProjPricingLeasePriceRSP lib2Rsp(ProjPricingLeasePriceLib f) {
        ProjPricingLeasePriceRSP rsp = BeanUtil.copyProperties(f, ProjPricingLeasePriceRSP.class);
        rsp.setId(f.getOriginId());
        // 字段不一致的需要手动拷贝
        rsp.setApprovedAmount(f.getProjectApprovalAmount());
        return rsp;
    }

    @Override
    public ProjPricingInfoModule getSubModule() {
        return ProjPricingInfoModule.ZL_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
