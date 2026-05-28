package cn.zswltech.mithras.service.controller.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.DashboardAssetsClientDistributionApi;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionAssetsIndustryListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionClientDepartmentListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionClientStatisticsListRSP;
import cn.zswltech.mithras.service.service.dashboard.boss.DashboardAssetsClientDistributionService;
import lombok.extern.slf4j.Slf4j;
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
public class DashboardAssetsClientDistributionController implements DashboardAssetsClientDistributionApi {
    @Resource
    private DashboardAssetsClientDistributionService dashboardAssetsClientDistributionService;

    @Override
    public R<List<DistributionAssetsIndustryListRSP>> getDistributionAssetsIndustryList() {
        return R.ok(dashboardAssetsClientDistributionService.listIndustryDistribution());
    }

    @Override
    public R<List<DistributionClientDepartmentListRSP>> getDistributionClientDepartmentList() {
        return R.ok(dashboardAssetsClientDistributionService.listClientByDept());
    }

    @Override
    public R<List<DistributionClientStatisticsListRSP>> getDistributionClientStatisticsList() {
        return R.ok(dashboardAssetsClientDistributionService.listClientStatistics());
    }
}
