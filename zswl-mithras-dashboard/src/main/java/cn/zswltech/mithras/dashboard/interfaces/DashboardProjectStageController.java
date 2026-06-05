package cn.zswltech.mithras.dashboard.interfaces;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardProjectStageApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.dashboard.application.DashboardProjectStageApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Slf4j
@RestController
public class DashboardProjectStageController implements DashboardProjectStageApi {
    @Resource
    private DashboardProjectStageApplicationService dashboardProjectStageService;

    @Override
    public R<List<DashboardProjectStageStatisticsRSP>> statisticsList(@Valid DashboardProjectStageStatisticsREQ req) {
        try {
            return R.ok(dashboardProjectStageService.statisticsList(req));
        } catch (Exception e) {
            log.error("业务工作台-项目视图-项目阶段-统计发生异常", e);
            throw new MithrasException("系统繁忙，请稍后再试");
        }
    }

    @Override
    public R<EstablishDetailSumRSP> establishList(@Valid DashboardProjectStageEstablishDetailREQ req) {
        return R.ok(dashboardProjectStageService.establishList(req));
    }

    @Override
    public R<ReviewDetailSumRSP> reviewList(@Valid DashboardProjectStageReviewDetailREQ req) {
        return R.ok(dashboardProjectStageService.reviewList(req, 1));
    }

    @Override
    public R<ReviewDetailSumRSP> reviewFinishNoContractList(@Valid DashboardProjectStageReviewDetailREQ req) {
        return R.ok(dashboardProjectStageService.reviewList(req, 2));
    }

    @Override
    public R<ReviewDetailSumRSP> reviewLegalReportList(@Valid DashboardProjectStageReviewDetailREQ req) {
        return R.ok(dashboardProjectStageService.reviewList(req, 3));
    }

    @Override
    public R<ContractDetailSumRSP> contractList(@Valid DashboardProjectStageContractDetailREQ req) {
        return R.ok(dashboardProjectStageService.contractList(req));
    }

    @Override
    public R<PaymentDetailSumRSP> preparePaymentList(@Valid DashboardProjectStagePaymentDetailREQ req) {
        return R.ok(dashboardProjectStageService.paymentList(req, 1));
    }

    @Override
    public R<PaymentDetailSumRSP> paymentList(@Valid DashboardProjectStagePaymentDetailREQ req) {
        return R.ok(dashboardProjectStageService.paymentList(req, 2));
    }

    @Override
    public R<RePaymentDetailSumRSP> repaymentList(@Valid DashboardProjectStageRepaymentDetailREQ req) {
        return R.ok(dashboardProjectStageService.repaymentList(req));
    }
}
