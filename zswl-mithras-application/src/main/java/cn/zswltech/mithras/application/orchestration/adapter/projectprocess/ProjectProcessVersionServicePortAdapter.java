package cn.zswltech.mithras.application.orchestration.adapter.projectprocess;

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
import cn.zswltech.mithras.projectprocess.versioning.ProjectProcessVersionServicePort;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingAocPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingCashFlowPlanService;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingFactoringPriceService;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingLeasePriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewCashFlowPlanService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewFactoringPriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewPriceService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ProjectProcessVersionServicePortAdapter implements ProjectProcessVersionServicePort {
    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ProjPricingAocPriceService projPricingAocPriceService;
    @Resource
    private ProjPricingLeasePriceService projPricingLeasePriceService;
    @Resource
    private ProjPricingFactoringPriceService projPricingFactoringPriceService;
    @Resource
    private ProjReviewAocPriceService projReviewAocPriceService;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;
    @Resource
    private ProjReviewFactoringPriceService projReviewFactoringPriceService;
    @Resource
    private ProjPricingCashFlowPlanService projPricingCashFlowPlanService;
    @Resource
    private ProjReviewCashFlowPlanService projReviewCashFlowPlanService;

    @Override
    public ProjReviewBaseInfo getReviewByPricing(ProjPricingBaseInfo pricingBaseInfo) {
        return projReviewBaseInfoService.getReviewByPricing(pricingBaseInfo);
    }

    @Override
    public ProjPricingBaseInfo getPricingByReview(ProjReviewBaseInfo reviewBaseInfo) {
        return projPricingBaseInfoService.getPricingByReview(reviewBaseInfo);
    }

    @Override
    public ProjPricingBaseInfoDetailRSP getPricingBaseInfoDetail(Long id, String processInstanceId) {
        return projPricingBaseInfoService.detail(id, processInstanceId);
    }

    @Override
    public ProjReviewBaseInfoDetailRSP getReviewBaseInfoDetail(Long id, String processInstanceId) {
        return projReviewBaseInfoService.detail(id, processInstanceId);
    }

    @Override
    public ProjReviewPriceDetailRSP getReviewOldPriceDetail(Long projectId) {
        return projReviewPriceService.oldDetail(projectId);
    }

    @Override
    public ProjPricingPriceDetailRSP getPricingOldPriceDetail(Long projectId) {
        return projPricingPriceService.oldDetail(projectId);
    }

    @Override
    public ProjPricingAocPrice getPricingAocPrice(Long projectId) {
        return projPricingAocPriceService.getByProjectId(projectId);
    }

    @Override
    public ProjPricingLeasePrice getPricingLeasePrice(Long projectId) {
        return projPricingLeasePriceService.getByProjectId(projectId);
    }

    @Override
    public ProjPricingFactoringPrice getPricingFactoringPrice(Long projectId) {
        return projPricingFactoringPriceService.getByProjectId(projectId);
    }

    @Override
    public ProjReviewAocPrice getReviewAocPrice(Long projectId) {
        return projReviewAocPriceService.getByProjectId(projectId);
    }

    @Override
    public ProjReviewLeasePrice getReviewLeasePrice(Long projectId) {
        return projReviewLeasePriceService.getByProjectId(projectId);
    }

    @Override
    public ProjReviewFactoringPrice getReviewFactoringPrice(Long projectId) {
        return projReviewFactoringPriceService.getByProjectId(projectId);
    }

    @Override
    public List<ProjPricingCashFlowPlan> listPricingCashFlowPlan(Long projectId, String version) {
        return projPricingCashFlowPlanService.listByProjPricingId(projectId, version);
    }

    @Override
    public List<ProjReviewCashFlowPlan> listReviewCashFlowPlan(Long projectId, String version) {
        return projReviewCashFlowPlanService.listByProjReviewId(projectId, version);
    }
}
