package cn.zswltech.mithras.projectprocess.application.lib;

import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;

import java.util.List;

public interface ProjectProcessVersionServicePort {
    ProjReviewBaseInfo getReviewByPricing(ProjPricingBaseInfo pricingBaseInfo);

    ProjPricingBaseInfo getPricingByReview(ProjReviewBaseInfo reviewBaseInfo);

    ProjPricingBaseInfoDetailRSP getPricingBaseInfoDetail(Long id, String processInstanceId);

    ProjReviewBaseInfoDetailRSP getReviewBaseInfoDetail(Long id, String processInstanceId);

    ProjReviewPriceDetailRSP getReviewOldPriceDetail(Long projectId);

    ProjPricingPriceDetailRSP getPricingOldPriceDetail(Long projectId);

    ProjPricingAocPrice getPricingAocPrice(Long projectId);

    ProjPricingLeasePrice getPricingLeasePrice(Long projectId);

    ProjPricingFactoringPrice getPricingFactoringPrice(Long projectId);

    ProjReviewAocPrice getReviewAocPrice(Long projectId);

    ProjReviewLeasePrice getReviewLeasePrice(Long projectId);

    ProjReviewFactoringPrice getReviewFactoringPrice(Long projectId);

    List<ProjPricingCashFlowPlan> listPricingCashFlowPlan(Long projectId, String version);

    List<ProjReviewCashFlowPlan> listReviewCashFlowPlan(Long projectId, String version);
}
