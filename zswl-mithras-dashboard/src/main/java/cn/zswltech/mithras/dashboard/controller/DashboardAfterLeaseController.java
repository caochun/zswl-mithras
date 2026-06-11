package cn.zswltech.mithras.dashboard.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardAfterLeaseApi;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseCheckListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseStatisticsRSP;
import cn.zswltech.mithras.dashboard.enums.DashboardAfterLeaseCheckStatueEnum;
import cn.zswltech.mithras.dashboard.application.DashboardAfterLeaseApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
public class DashboardAfterLeaseController implements DashboardAfterLeaseApi {

    @Resource
    private DashboardAfterLeaseApplicationService dashboardAfterLeaseService;

    @Override
    public R<List<DashboardAfterLeaseStatisticsRSP>> statisticsList() {
        return R.ok(dashboardAfterLeaseService.statisticsList());
    }

    @Override
    public R<List<DashboardAfterLeaseCheckRSP>> survivalPageList(@Valid DashboardAfterLeaseCheckListREQ req) {
        List<DashboardAfterLeaseCheckRSP> rsps = new ArrayList<>();
        if (ObjectUtil.equals(req.getCheckPlanStatus(), DashboardAfterLeaseCheckStatueEnum.CHECKING.name())) {
            rsps.addAll(dashboardAfterLeaseService.afterLeaseCheckList(req));
        } else {
            rsps.addAll(dashboardAfterLeaseService.afterLeaseCheckList(req));
            rsps.addAll(dashboardAfterLeaseService.afterLeaseCheckPrepareList(req));
        }
        return R.ok(rsps);
    }
}
