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

@Api(tags = "业务工作台-运营部-合同审批时效及退回情况")
@RequestMapping(path = "/dashboard/operation")
public interface DashboardOperationContractApi {

    @ApiOperation("业务工作台-合同审批时效及退回情况-运营审批时效")
    @PostMapping(path = "/approval/list")
    R<List<DashboardApprovalListRSP>> approvalList(@RequestBody @Valid DashboardApprovalListREQ req);

    @ApiOperation("业务工作台-项目视图-计划执行情况-运营审批时效统计")
    @PostMapping(path = "/approval/statistics")
    R<DashboardOperationApprovalStatisticsRSP> approvalStatistics(@RequestBody @Valid DashboardApprovalListREQ req);

    @ApiOperation("业务工作台-合同审批时效及退回情况-合同退回列表")
    @PostMapping(path = "/contract/return/list")
    R<List<DashboardContractReturnListRSP>> contractReturnList(@RequestBody @Valid DashboardContractReturnListREQ req);

    @ApiOperation("业务工作台-项目视图-计划执行情况-合同退回统计")
    @PostMapping(path = "/contract/return/statistics")
    R<DashboardOperationContractReturnStatisticsRSP> contractReturnStatistics(@RequestBody @Valid DashboardContractReturnListREQ req);

}
