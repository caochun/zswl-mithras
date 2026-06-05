package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseCheckListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseStatisticsRSP;

import java.util.List;

public interface DashboardAfterLeaseApplicationService {

    List<DashboardAfterLeaseStatisticsRSP> statisticsList();

    List<DashboardAfterLeaseCheckRSP> afterLeaseCheckList(DashboardAfterLeaseCheckListREQ req);

    List<DashboardAfterLeaseCheckRSP> afterLeaseCheckPrepareList(DashboardAfterLeaseCheckListREQ req);
}
