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
 * @date 2024/6/16
 * @description
 */
@Api(tags = "业务工作台-项目视图-项目信息")
@RequestMapping(path = "/dashboard/project/info")
public interface DashboardProjectInfoApi {
    @ApiOperation("业务工作台-项目视图-项目信息-统计")
    @PostMapping(path = "/statistics")
    R<List<DashboardProjectInfoStatisticsRSP>> statisticsList(@RequestBody @Valid DashboardProjectInfoStatisticsListREQ req);

    @ApiOperation("业务工作台-项目视图-项目信息-3个月内结清项目明细")
    @PostMapping(path = "/settleinthreemonth/list")
    R<SettleInThreeMonSumRSP> settleInThreeMonthList(@RequestBody @Valid DashboardProjectInfoSettleInThreeMonthREQ req);

    @ApiOperation("业务工作台-项目视图-项目信息-存在逾期项目明细")
    @PostMapping(path = "/overdue/list")
    R<OverDueSumRSP> overdueList(@RequestBody @Valid DashboardProjectInfoOverdueREQ req);

    @ApiOperation("业务工作台-项目视图-项目信息-本月应收/实收租金明细")
    @PostMapping(path = "/rentthismonth/list")
    R<RentThisMonthSumRSP> rentThisMonthList(@RequestBody @Valid DashboardProjectInfoRentThisMonthREQ req);

    @ApiOperation("业务工作台-项目视图-项目情况-统计")
    @PostMapping(path = "/statisticsRentInfoList")
    R<List<DashboardProjectFinanceStatisticsRSP>> statisticsRentInfoList();

    @ApiOperation(("业务工作台-项目视图-项目情况-已投放未结清项目"))
    @PostMapping(path = "/paynosettle/list")
    R<Map<String, Object>> listProjectPayNoSettle(@RequestBody @Valid DashboardProjectPayNoSettleREQ req);

    @ApiOperation("业务工作台-项目视图-项目情况-剩余本金与拨备")
    @PostMapping(path = "/provision/list")
    R<Map<String, Object>> listProvision(@RequestBody @Valid DashboardProjectProvisionREQ req);

    @ApiOperation("业务工作台-项目视图-项目情况-质押/监管情况")
    @PostMapping(path = "/pledge/list")
    R<Map<String, Object>> listPledge(@RequestBody @Valid DashboardProjectPledgeREQ req);
}
