package cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessSurvivingContractResolver;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.ProjReviewLibAbstractHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjReviewFactoringPriceLibHandler
        extends ProjReviewLibAbstractHandler<ProjReviewFactoringPriceLib, ProjReviewFactoringPrice, ProjReviewFactoringPriceRSP> {
    @Resource
    private ProjectProcessSurvivingContractResolver survivingContractResolver;

    @Override
    protected ProjReviewFactoringPriceLib entity2Lib(ProjReviewFactoringPrice f) {
        return BeanUtil.copyProperties(f, ProjReviewFactoringPriceLib.class);
    }

    @Override
    protected ProjReviewFactoringPrice lib2Entity(ProjReviewFactoringPriceLib t) {
        return BeanUtil.copyProperties(t, ProjReviewFactoringPrice.class);
    }

    @Override
    protected ProjReviewFactoringPriceRSP lib2Rsp(ProjReviewFactoringPriceLib f) {
        ProjReviewFactoringPriceRSP rsp = BeanUtil.copyProperties(f, ProjReviewFactoringPriceRSP.class);
        rsp.setContracts(survivingContractResolver.resolveProjReviewSurvivingContracts(rsp.getProjectId()));
        rsp.setId(f.getOriginId());
        rsp.setApprovedAmount(f.getProjectApprovalAmount());
        return rsp;
    }

    @Override
    public ProjReviewInfoModule getSubModule() {
        return ProjReviewInfoModule.BL_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
