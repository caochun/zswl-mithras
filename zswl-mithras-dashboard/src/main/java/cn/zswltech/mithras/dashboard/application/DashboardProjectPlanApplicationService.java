package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;

public interface DashboardProjectPlanApplicationService {

    List<DashboardProjectPlanListRSP> planList(DashboardProjectPlanListREQ req);

    DashboardProjectPlanStatisticsRSP statistics(DashboardProjectPlanStatisticsREQ req);

    List<DashboardProjectPlanStatisticsByDeptRSP> statisticsGroupByDept(DashboardProjectPlanStatisticsREQ req);
}
