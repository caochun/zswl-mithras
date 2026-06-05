package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface DashboardPerformanceApplicationService {

    DeptPerformanceRSP deptPerformance() throws ExecutionException, InterruptedException;

    List<PersonalPerformanceRSP> personalPerformance();

    KpiDeptShipSortPerformanceRSP deptShipSortPerformance();

    List<DeptInSortPerformanceRSP> deptInSortPerformance();
}
