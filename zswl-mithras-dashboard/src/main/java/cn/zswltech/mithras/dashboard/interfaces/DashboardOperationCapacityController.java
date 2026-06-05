package cn.zswltech.mithras.dashboard.interfaces;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardOperationCapacityApi;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import cn.zswltech.mithras.dashboard.application.DashboardOperationCapacityApplicationService;
import cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Slf4j
public class DashboardOperationCapacityController implements DashboardOperationCapacityApi {
    @Resource
    private DashboardOperationCapacityApplicationService dashboardOperationCapacityService;
    private static final String RECORDS = "records";
    private static final String SUM_DATA = "sumData";
    private static final String AVERAGE_DATA = "averageData";

    @Override
    public R<Map<String, Object>> list(DashboardOperationCapacityListREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardOperationCapacityListRSP> rspList = dashboardOperationCapacityService.capacityList(req);
        map.put(RECORDS, rspList);
        try {
            DashboardOperationCapacityListRSP sumData = DashboardHelpUtil.countValueUnitDTO(rspList, new DashboardOperationCapacityListRSP());
            DashboardOperationCapacityListRSP averageSum = DashboardHelpUtil.averageValue(rspList, sumData, new DashboardOperationCapacityListRSP());
            sumData.setBizDeptName("总合计");
            averageSum.setBizDeptName("平均合计");
            map.put(SUM_DATA,sumData);
            map.put(AVERAGE_DATA,averageSum);
        } catch (Exception e) {
            log.warn("DashboardOperationPayController payList count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<List<DashboardOperationCapacityStatisticsRSP>> statistics(DashboardOperationCapacityStatisticsREQ req) {
        return R.ok(dashboardOperationCapacityService.statistics(req));
    }
}
