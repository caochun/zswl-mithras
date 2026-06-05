package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveREQ;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveRSP;

import java.util.List;

public interface DashboardOperateTodoApplicationService {

    List<DashboardOperateTodoArriveRSP> todoStatisticsArrive(DashboardOperateTodoArriveREQ req);

    List<DashboardOperateTodoArriveRSP> todoStatisticsWillArrive(DashboardOperateTodoArriveREQ req);
}
