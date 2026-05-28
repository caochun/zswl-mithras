package cn.zswltech.mithras.service.mapper.dashboard;

import cn.zswltech.mithras.service.mapper.model.dashboard.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@Mapper
public interface DashboardProjectInfoMapper {
    List<DashboardProjectInfoSettleInThreeMonthResult> listSettleInThreeMonth(DashboardProjectInfoSettleInThreeMonthQuery query);

    List<DashboardProjectInfoOverdueResult> listOverdue(DashboardProjectInfoOverdueQuery query);

    List<DashboardProjectInfoRentThisMonthResult> listRentThisMonth(DashboardProjectInfoRentThisMonthQuery query);

    List<DashboardProjectPayInfoResult> listPayInfo(DashboardProjectPayInfoQuery query);

    List<DashboardProjectPayNoSettleResult> listPayNoSettle(DashboardProjectPayNoSettleQuery query);

    List<DashboardProvisionResult> listProvision(DashboardProvisionQuery query);

    List<DashboardProjectPledgeResult> listProjPledge(DashboardProjectPledgeQuery query);

    List<DashboardProjectPlanInfoResult> listPlanInfo(DashboardProjectPlanInfoQuery query);
}
