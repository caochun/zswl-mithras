package cn.zswltech.mithras.service.controller.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardOperationConversionApi;
import cn.zswltech.mithras.api.dashboard.DashboardOperationTimeApi;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import cn.zswltech.mithras.service.service.dashboard.DashboardOperationConversionService;
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
 * @ClassName DashboardOperationConversionController
 * @Description TODO
 * @Author zhouning
 * @Date 2024/7/29 5:02 下午
 * @Version 1.0
 **/
@Slf4j
@RestController
public class DashboardOperationConversionController implements DashboardOperationConversionApi {

    private static final String RECORDS = "records";
    private static final String SUM_DATA = "sumData";
    private static final String AVERAGE_DATE = "averageData";

    @Resource
    private DashboardOperationConversionService dashboardOperationConversionService;

    @Override
    public R<List<DashboardOperationConversionStatisticsRSP>> timeStatistics(DashboardOperationConversionStatisticsREQ req) {
        return R.ok(dashboardOperationConversionService.timeList(req));
    }

    @Override
    public R<List<DashboardOperationConversionPercentageRSP>> timeTerm(DashboardOperationConversionStatisticsREQ req) {
        return R.ok(dashboardOperationConversionService.timeTerm(req));
    }

    @Override
    public R<Map<String, Object>> detailList(DashboardOperationConversionListREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardOperationConversionListRSP> rspList = dashboardOperationConversionService.detailList(req);
        map.put(RECORDS, rspList);
        try {
            DashboardOperationConversionListRSP sumData = DashboardHelpUtil.countValueUnitDTO(rspList, new DashboardOperationConversionListRSP(), Collections.singleton("setFinishRate"));
            DashboardOperationConversionListRSP averageData = DashboardHelpUtil.averageValue(rspList,sumData,new DashboardOperationConversionListRSP());
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
