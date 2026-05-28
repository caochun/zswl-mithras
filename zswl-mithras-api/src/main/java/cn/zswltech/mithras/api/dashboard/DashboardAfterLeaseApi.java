package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseCheckListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseStatisticsRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "业务工作台-租后管理-业务信息")
@RequestMapping(path = "/dashboard/after/lease")
public interface DashboardAfterLeaseApi {

    @ApiOperation("业务工作台-租后管理-统计")
    @PostMapping(path = "/check/statistics")
    R<List<DashboardAfterLeaseStatisticsRSP>> statisticsList();


    @ApiOperation("业务工作台-租后管理-统计")
    @PostMapping(path = "/check/list")
    R<List<DashboardAfterLeaseCheckRSP>> survivalPageList(@RequestBody @Valid DashboardAfterLeaseCheckListREQ req);

}
