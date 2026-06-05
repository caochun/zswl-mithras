package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface DashboardClientOverviewApplicationService {

    List<DashboardClientOverviewStatisticsRSP> statisticsList() throws ExecutionException, InterruptedException;

    PageR<DashboardClientOverviewAllRSP> allPageList(DashboardClientOverviewAllREQ req);

    PageR<DashboardClientOverviewSurvivalRSP> survivalPageList(DashboardClientOverviewSurvivalREQ req);

    PageR<DashboardClientOverviewSettleInThreeMonthRSP> settleInThreeMonthPageList(DashboardClientOverviewSettleInThreeMonthREQ req);

    PageR<DashboardClientOverviewOverdueRSP> overduePageList(DashboardClientOverviewOverdueREQ req);

    PageR<DashboardClientOverviewSettledRSP> settledPageList(DashboardClientOverviewSettledREQ req);
}
