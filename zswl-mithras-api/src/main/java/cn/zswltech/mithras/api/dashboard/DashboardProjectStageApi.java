package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Api(tags = "业务工作台-项目视图-项目阶段")
@RequestMapping(path = "/dashboard/project/stage")
public interface DashboardProjectStageApi {
    @ApiOperation("业务工作台-项目视图-项目阶段-各阶段统计")
    @PostMapping(path = "/statistics")
    R<List<DashboardProjectStageStatisticsRSP>> statisticsList(@RequestBody @Valid DashboardProjectStageStatisticsREQ req);

    @ApiOperation("业务工作台-项目视图-项目阶段-立项阶段明细")
    @PostMapping(path = "/projestablish/list")
    R<EstablishDetailSumRSP> establishList(@RequestBody @Valid DashboardProjectStageEstablishDetailREQ req);

    @ApiOperation("业务工作台-项目视图-项目阶段-评审阶段明细")
    @PostMapping(path = "/projreview/list")
    R<ReviewDetailSumRSP> reviewList(@RequestBody @Valid DashboardProjectStageReviewDetailREQ req);

    @ApiOperation("业务工作台-项目视图-项目阶段-评审结束未创建合同明细")
    @PostMapping(path = "/projreview/nocontract/list")
    R<ReviewDetailSumRSP> reviewFinishNoContractList(@RequestBody @Valid DashboardProjectStageReviewDetailREQ req);

    @ApiOperation("业务工作台-项目视图-项目阶段-待出具合规意见")
    @PostMapping(path = "/projreview/legalreport/list")
    R<ReviewDetailSumRSP> reviewLegalReportList(@RequestBody @Valid DashboardProjectStageReviewDetailREQ req);

    @ApiOperation("业务工作台-项目视图-项目阶段-签约阶段明细")
    @PostMapping(path = "/contract/list")
    R<ContractDetailSumRSP> contractList(@RequestBody @Valid DashboardProjectStageContractDetailREQ req);

    @ApiOperation("业务工作台-项目视图-项目阶段-付款阶段明细")
    @PostMapping(path = "/preparepayment/list")
    R<PaymentDetailSumRSP> preparePaymentList(@RequestBody @Valid DashboardProjectStagePaymentDetailREQ req);

    @ApiOperation("业务工作台-项目视图-项目阶段-投放阶段明细")
    @PostMapping(path = "/payment/list")
    R<PaymentDetailSumRSP> paymentList(@RequestBody @Valid DashboardProjectStagePaymentDetailREQ req);

    @ApiOperation("业务工作台-项目视图-项目阶段-还款阶段明细")
    @PostMapping(path = "/repayment/list")
    R<RePaymentDetailSumRSP> repaymentList(@RequestBody @Valid DashboardProjectStageRepaymentDetailREQ req);
}
