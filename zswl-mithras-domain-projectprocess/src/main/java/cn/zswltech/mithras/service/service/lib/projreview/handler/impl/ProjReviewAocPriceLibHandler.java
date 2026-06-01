package cn.zswltech.mithras.service.service.lib.projreview.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.service.service.lib.projreview.handler.ProjReviewLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjReviewAocPriceLibHandler
        extends ProjReviewLibAbstractHandler<ProjReviewAocPriceLib, ProjReviewAocPrice, ProjReviewAocPriceRSP> {

    @Override
    protected ProjReviewAocPriceLib entity2Lib(ProjReviewAocPrice f) {
        return BeanUtil.copyProperties(f, ProjReviewAocPriceLib.class);
    }

    @Override
    protected ProjReviewAocPrice lib2Entity(ProjReviewAocPriceLib t) {
        return BeanUtil.copyProperties(t, ProjReviewAocPrice.class);
    }

    @Override
    protected ProjReviewAocPriceRSP lib2Rsp(ProjReviewAocPriceLib f) {
        ProjReviewAocPriceRSP rsp = BeanUtil.copyProperties(f, ProjReviewAocPriceRSP.class);
        rsp.setApprovedAmount(f.getProjectApprovalAmount());
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjReviewInfoModule getSubModule() {
        return ProjReviewInfoModule.ZR_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
