package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;

public interface DashboardProjectInfoApplicationService {

    List<DashboardProjectInfoStatisticsRSP> statisticsList(DashboardProjectInfoStatisticsListREQ req);

    SettleInThreeMonSumRSP listSettleInThreeMonth(DashboardProjectInfoSettleInThreeMonthREQ req);

    OverDueSumRSP listOverdue(DashboardProjectInfoOverdueREQ req);

    RentThisMonthSumRSP listRentThisMonth(DashboardProjectInfoRentThisMonthREQ req);

    List<DashboardProjectFinanceStatisticsRSP> financeStatisticsList() throws Exception;

    List<DashboardProjectPayNoSettleRSP> listPayNoSettle(DashboardProjectPayNoSettleREQ req);

    List<DashboardProjectProvisionRSP> listProvision(DashboardProjectProvisionREQ req);

    List<DashboardProjectPledgeRSP> listPledge(DashboardProjectPledgeREQ req);
}
