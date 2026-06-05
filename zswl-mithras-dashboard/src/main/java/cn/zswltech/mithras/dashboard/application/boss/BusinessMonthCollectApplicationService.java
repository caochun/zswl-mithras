package cn.zswltech.mithras.dashboard.application.boss;

import cn.zswltech.mithras.dto.dashboard.boss.MonthCollectStatisticsListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.MonthCollectStatisticsListRSP;

import java.util.List;

public interface BusinessMonthCollectApplicationService {

    List<MonthCollectStatisticsListRSP> getMonthCollectStatisticsList(MonthCollectStatisticsListREQ req);
}
