package cn.zswltech.mithras.service.controller.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardOperationPayApi;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanListRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayStatisticsREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayStatisticsRSP;
import cn.zswltech.mithras.service.service.dashboard.DashboardOperationPayService;
import cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class DashboardOperationPayController implements DashboardOperationPayApi {
    @Resource
    private DashboardOperationPayService dashboardOperationPayService;
    private static final String RECORDS = "records";
    private static final String SUM_DATA = "sumData";
    private static final String AVERAGE_DATA = "averageData";

    @Override
    public R<Map<String,Object>> payList(DashboardOperationPayListREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardOperationPayListRSP> rspList = dashboardOperationPayService.payList(req);
        map.put(RECORDS, rspList);
        try {
            DashboardOperationPayListRSP sumData = DashboardHelpUtil.countValueUnitDTO(rspList, new DashboardOperationPayListRSP(), Collections.singleton("setFinishRate"));
            DashboardOperationPayListRSP averageData = DashboardHelpUtil.averageValue(rspList,sumData,new DashboardOperationPayListRSP());
            sumData.setBizDeptName("总合计");
            averageData.setBizDeptName("平均合计");
            map.put(SUM_DATA,sumData);
            map.put(AVERAGE_DATA,averageData);
        } catch (Exception e) {
            log.warn("DashboardOperationPayController payList count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<List<DashboardOperationPayStatisticsRSP>> payStatistics(DashboardOperationPayStatisticsREQ req) {
        return R.ok(dashboardOperationPayService.payStatistics(req));
    }

    @Override
    public R<Void> importExcel(MultipartFile file, String fileType) {
        dashboardOperationPayService.importExcel(file,fileType);
        return R.ok();
    }
}
