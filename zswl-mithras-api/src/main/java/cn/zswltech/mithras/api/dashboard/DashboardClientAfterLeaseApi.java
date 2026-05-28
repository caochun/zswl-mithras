package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseStatisticsRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Api(tags = "业务工作台-客户视图-租后管理")
@RequestMapping(path = "/dashboard/client/afterlease")
public interface DashboardClientAfterLeaseApi {
    @ApiOperation("业务工作台-客户视图-租后管理-统计")
    @PostMapping(path = "/statistics")
    R<List<DashboardClientAfterLeaseStatisticsRSP>> statisticsList();

    @ApiOperation("业务工作台-客户视图-租后管理-租后检查")
    @PostMapping(path = "/check/list")
    R<PageR<DashboardClientAfterLeaseCheckRSP>> afterLeaseCheckList(@RequestBody DashboardClientAfterLeaseCheckREQ req);
}
