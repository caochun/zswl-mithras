package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;

public interface DashboardProjectPayApplicationService {

    List<DashboardProjectPayListRSP> list(DashboardProjectPayListREQ req);

    DashboardProjectPayStatisticsRSP statistics(DashboardProjectPayStatisticsREQ req);

    List<DashboardProjectPayStatisticsByDeptRSP> statisticsListByDept(DashboardProjectPayStatisticsREQ req);
}
