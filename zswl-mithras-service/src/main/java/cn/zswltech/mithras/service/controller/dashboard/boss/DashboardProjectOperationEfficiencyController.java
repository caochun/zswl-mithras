package cn.zswltech.mithras.service.controller.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.DashboardProjectOperationEfficiencyApi;
import cn.zswltech.mithras.dto.dashboard.boss.OperationEfficiencyDetailListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.OperationEfficiencyStatisticsListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.OperationEfficiencyStatisticsListRSP;
import cn.zswltech.mithras.service.service.dashboard.boss.DashboardProjectOperationEfficiencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/19:23
 * @description
 */
@Slf4j
@RestController
public class DashboardProjectOperationEfficiencyController implements DashboardProjectOperationEfficiencyApi {
    @Resource
    private DashboardProjectOperationEfficiencyService dashboardProjectOperationEfficiencyService;

    @Override
    public R<List<OperationEfficiencyStatisticsListRSP>> getEfficiencyStatisticsList(OperationEfficiencyStatisticsListREQ req) {
        return R.ok(dashboardProjectOperationEfficiencyService.listEfficiency(req.getType()));
    }

    @Override
    public R<List<OperationEfficiencyDetailListRSP>> list(@RequestBody OperationEfficiencyStatisticsListREQ req) {
        return R.ok(dashboardProjectOperationEfficiencyService.listEfficiencyGroupByDept(req.getType()));
    }
}
