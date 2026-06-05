package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;

public interface DashboardProjectStageApplicationService {

    List<DashboardProjectStageStatisticsRSP> statisticsList(DashboardProjectStageStatisticsREQ req) throws Exception;

    EstablishDetailSumRSP establishList(DashboardProjectStageEstablishDetailREQ req);

    ReviewDetailSumRSP reviewList(DashboardProjectStageReviewDetailREQ req, Integer viewType);

    ContractDetailSumRSP contractList(DashboardProjectStageContractDetailREQ req);

    PaymentDetailSumRSP paymentList(DashboardProjectStagePaymentDetailREQ req, Integer viewType);

    RePaymentDetailSumRSP repaymentList(DashboardProjectStageRepaymentDetailREQ req);
}
