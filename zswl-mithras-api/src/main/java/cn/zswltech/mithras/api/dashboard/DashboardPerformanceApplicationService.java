package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.dto.dashboard.DeptInSortPerformanceRSP;
import cn.zswltech.mithras.dto.dashboard.DeptPerformanceRSP;
import cn.zswltech.mithras.dto.dashboard.KpiDeptShipSortPerformanceRSP;
import cn.zswltech.mithras.dto.dashboard.PersonalPerformanceRSP;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface DashboardPerformanceApplicationService {

    DeptPerformanceRSP deptPerformance() throws ExecutionException, InterruptedException;

    List<PersonalPerformanceRSP> personalPerformance();

    KpiDeptShipSortPerformanceRSP deptShipSortPerformance();

    List<DeptInSortPerformanceRSP> deptInSortPerformance();
}
