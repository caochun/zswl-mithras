package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanStatisticsByDeptRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanStatisticsREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanStatisticsRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "业务工作台-项目视图-计划执行情况")
@RequestMapping(path = "/dashboard/plan")
public interface DashboardProjectPlanApi {
    @ApiOperation("业务工作台-项目视图-计划执行情况")
    @PostMapping(path = "/list")
    R<Map<String, Object>> planList(@RequestBody @Valid DashboardProjectPlanListREQ req);

    @ApiOperation("业务工作台-项目视图-计划执行情况-按公司统计")
    @PostMapping(path = "/statistics")
    R<DashboardProjectPlanStatisticsRSP> statistics(@RequestBody @Valid DashboardProjectPlanStatisticsREQ req);

    @ApiOperation("业务工作台-项目视图-计划执行情况-按部门统计")
    @PostMapping(path = "/statistics/bydept")
    R<List<DashboardProjectPlanStatisticsByDeptRSP>> statisticsGroupByDept(@RequestBody @Valid DashboardProjectPlanStatisticsREQ req);
}
