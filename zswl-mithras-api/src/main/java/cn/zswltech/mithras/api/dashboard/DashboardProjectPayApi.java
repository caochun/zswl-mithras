package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2024/6/23
 * @description
 */
@Api(tags = "业务工作台-项目视图-投放情况")
@RequestMapping(path = "/dashboard/pay")
public interface DashboardProjectPayApi {
    @ApiOperation("业务工作台-项目视图-投放情况")
    @PostMapping(path = "/list")
    R<Map<String, Object>> actualPayList(@RequestBody @Valid DashboardProjectPayListREQ req);

    @ApiOperation("业务工作台-项目视图-投放情况-按公司统计")
    @PostMapping(path = "/statistics")
    R<DashboardProjectPayStatisticsRSP> statistics(@RequestBody @Valid DashboardProjectPayStatisticsREQ req);

    @ApiOperation("业务工作台-项目视图-投放情况-按部门统计")
    @PostMapping(path = "/statistics/bydept")
    R<List<DashboardProjectPayStatisticsByDeptRSP>> statisticsGroupByDept(@RequestBody @Valid DashboardProjectPayStatisticsREQ req);
}
