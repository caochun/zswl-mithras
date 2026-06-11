package cn.zswltech.mithras.dashboard.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardProjectPayApi;
import cn.zswltech.mithras.api.dashboard.DashboardProjectPlanApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dashboard.application.DashboardProjectPayApplicationService;
import cn.zswltech.mithras.dashboard.application.DashboardProjectPlanApplicationService;
import cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class DashboardProjectPlanController implements DashboardProjectPlanApi {
    @Resource
    private DashboardProjectPlanApplicationService dashboardProjectPlanInfoService;
    private static final String RECORDS = "records";
    private static final String SUM_DATA = "sumData";

    @Override
    public R<Map<String, Object>> planList(DashboardProjectPlanListREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardProjectPlanListRSP> rspList = dashboardProjectPlanInfoService.planList(req);
        map.put(RECORDS, rspList);
        try {
            DashboardProjectPlanListRSP sumData = DashboardHelpUtil.countValueUnitDTO(rspList, new DashboardProjectPlanListRSP());
            sumData.setIrr(null);
            sumData.setContractLimitYear(null);
            map.put(SUM_DATA,sumData);
        } catch (Exception e) {
            log.warn("DashboardProjectPlanController planList count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<DashboardProjectPlanStatisticsRSP> statistics(DashboardProjectPlanStatisticsREQ req) {
        return R.ok(dashboardProjectPlanInfoService.statistics(req));
    }

    @Override
    public R<List<DashboardProjectPlanStatisticsByDeptRSP>> statisticsGroupByDept(DashboardProjectPlanStatisticsREQ req) {
        return R.ok(dashboardProjectPlanInfoService.statisticsGroupByDept(req));
    }
}
