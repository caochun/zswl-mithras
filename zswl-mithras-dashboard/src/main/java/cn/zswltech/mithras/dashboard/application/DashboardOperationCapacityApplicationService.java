package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationCapacityListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationCapacityListRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationCapacityStatisticsREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationCapacityStatisticsRSP;

import java.util.List;

public interface DashboardOperationCapacityApplicationService {

    List<DashboardOperationCapacityListRSP> capacityList(DashboardOperationCapacityListREQ req);

    List<DashboardOperationCapacityStatisticsRSP> statistics(DashboardOperationCapacityStatisticsREQ req);
}
