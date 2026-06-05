package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;

public interface DashboardOperationContractApplicationService {

    List<DashboardApprovalListRSP> approvalList(DashboardApprovalListREQ req);

    List<DashboardApprovalListRSP> approvalListGuanYuan(DashboardApprovalListREQ req);

    DashboardOperationApprovalStatisticsRSP approvalStatistics(DashboardApprovalListREQ req);

    List<DashboardContractReturnListRSP> contractReturnList(DashboardContractReturnListREQ req);

    DashboardOperationContractReturnStatisticsRSP contractReturnStatistics(DashboardContractReturnListREQ req);
}
