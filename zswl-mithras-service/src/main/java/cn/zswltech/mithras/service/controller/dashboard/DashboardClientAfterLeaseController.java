package cn.zswltech.mithras.service.controller.dashboard;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardClientAfterLeaseApi;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseStatisticsRSP;
import cn.zswltech.mithras.service.service.dashboard.DashboardClientAfterLeaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/6/18/14:09
 * @description
 */
@Slf4j
@RestController
public class DashboardClientAfterLeaseController implements DashboardClientAfterLeaseApi {

    @Resource
    private DashboardClientAfterLeaseService dashboardClientAfterLeaseService;

    @Override
    public R<List<DashboardClientAfterLeaseStatisticsRSP>> statisticsList() {
        return R.ok(dashboardClientAfterLeaseService.statisticsList());
    }

    @Override
    public R<PageR<DashboardClientAfterLeaseCheckRSP>> afterLeaseCheckList(DashboardClientAfterLeaseCheckREQ req) {
        return R.ok(dashboardClientAfterLeaseService.afterLeaseCheckList(req));
    }
}
