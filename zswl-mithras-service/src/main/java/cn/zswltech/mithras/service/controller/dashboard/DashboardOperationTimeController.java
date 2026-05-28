package cn.zswltech.mithras.service.controller.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardOperationTimeApi;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import cn.zswltech.mithras.service.service.dashboard.DashboardOperationTimeService;
import cn.zswltech.mithras.service.util.DashboardHelpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DashboardOperationContractController
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/24 5:02 下午
 * @Version 1.0
 **/
@Slf4j
@RestController
public class DashboardOperationTimeController implements DashboardOperationTimeApi {

    private static final String RECORDS = "records";
    private static final String SUM_DATA = "sumData";
    private static final String AVERAGE_DATE = "averageData";

    @Resource
    private DashboardOperationTimeService dashboardOperationTimeService;

    @Override
    public R<List<DashboardOperationTimeStatisticsRSP>> timeStatistics(DashboardOperationTimeStatisticsREQ req) {
        return R.ok(dashboardOperationTimeService.timeList(req));
    }

    @Override
    public R<List<DashboardOperationTimePercentageRSP>> timeTerm(DashboardOperationTimeStatisticsREQ req) {
        return R.ok(dashboardOperationTimeService.timeTerm(req));
    }

    @Override
    public R<Map<String, Object>> detailList(DashboardOperationTimeListREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardOperationTimeStatisticsRSP> rspList = dashboardOperationTimeService.detailList(req);
        map.put(RECORDS, rspList);
        try {
            DashboardOperationTimeStatisticsRSP sumData = DashboardHelpUtil.countValueUnitDTO(rspList, new DashboardOperationTimeStatisticsRSP(), Collections.singleton("setFinishRate"));
            DashboardOperationTimeStatisticsRSP averageData = DashboardHelpUtil.averageValue(rspList,sumData,new DashboardOperationTimeStatisticsRSP());
            sumData.setBizDeptName("总合计");
            averageData.setBizDeptName("平均合计");
            map.put(SUM_DATA,sumData);
            map.put(AVERAGE_DATE,averageData);
        } catch (Exception e) {
            log.warn("DashboardOperationTimeController detailList count error ", e);
        }
        return R.ok(map);
    }
}
