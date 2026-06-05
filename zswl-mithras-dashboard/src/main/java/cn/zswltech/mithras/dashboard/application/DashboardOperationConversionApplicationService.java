package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.operation.*;

import java.util.List;

public interface DashboardOperationConversionApplicationService {

    List<DashboardOperationConversionStatisticsRSP> timeList(DashboardOperationConversionStatisticsREQ req);

    List<DashboardOperationConversionPercentageRSP> timeTerm(DashboardOperationConversionStatisticsREQ req);

    List<DashboardOperationConversionListRSP> detailList(DashboardOperationConversionListREQ req);
}
