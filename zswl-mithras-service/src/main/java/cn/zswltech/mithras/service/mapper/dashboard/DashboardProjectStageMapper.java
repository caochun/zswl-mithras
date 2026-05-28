package cn.zswltech.mithras.service.mapper.dashboard;

import cn.zswltech.mithras.service.mapper.model.dashboard.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Mapper
public interface DashboardProjectStageMapper {
    List<DashboardProjectStageEstablishResult> listProjectOverviewEstablishStage(DashboardProjectStageEstablishQuery query);

    List<DashboardProjectStageReviewResult> listProjectOverviewReviewStage(DashboardProjectStageReviewQuery query);

    List<DashboardProjectStageContractResult> listProjectOverviewContractStage(DashboardProjectStageContractQuery query);

    List<DashboardProjectStagePaymentResult> listProjectOverviewPaymentStage(DashboardProjectStagePaymentQuery query);

    List<DashboardProjectStageRepaymentResult> listProjectOverviewRepaymentStage(DashboardProjectStageRepaymentQuery query);
}
