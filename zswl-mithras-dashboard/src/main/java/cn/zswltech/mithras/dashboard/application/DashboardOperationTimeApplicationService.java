package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.operation.*;

import java.util.List;

public interface DashboardOperationTimeApplicationService {

    List<DashboardOperationTimeStatisticsRSP> timeList(DashboardOperationTimeStatisticsREQ req);

    List<DashboardOperationTimePercentageRSP> timeTerm(DashboardOperationTimeStatisticsREQ req);

    List<DashboardOperationTimeStatisticsRSP> detailList(DashboardOperationTimeListREQ req);
}
