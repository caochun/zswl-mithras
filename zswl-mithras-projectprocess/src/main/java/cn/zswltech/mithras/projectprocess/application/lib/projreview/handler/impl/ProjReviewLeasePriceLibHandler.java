package cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.ProjReviewLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjReviewLeasePriceLibHandler
        extends ProjReviewLibAbstractHandler<ProjReviewLeasePriceLib, ProjReviewLeasePrice, ProjReviewLeasePriceRSP> {

    @Override
    protected ProjReviewLeasePriceLib entity2Lib(ProjReviewLeasePrice f) {
        return BeanUtil.copyProperties(f, ProjReviewLeasePriceLib.class);
    }

    @Override
    protected ProjReviewLeasePrice lib2Entity(ProjReviewLeasePriceLib t) {
        return BeanUtil.copyProperties(t, ProjReviewLeasePrice.class);
    }

    @Override
    protected ProjReviewLeasePriceRSP lib2Rsp(ProjReviewLeasePriceLib f) {
        ProjReviewLeasePriceRSP rsp = BeanUtil.copyProperties(f, ProjReviewLeasePriceRSP.class);
        rsp.setId(f.getOriginId());
        rsp.setApprovedAmount(f.getProjectApprovalAmount());
        return rsp;
    }

    @Override
    public ProjReviewInfoModule getSubModule() {
        return ProjReviewInfoModule.ZL_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
