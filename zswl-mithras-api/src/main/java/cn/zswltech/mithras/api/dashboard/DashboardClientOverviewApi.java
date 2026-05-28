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
@Api(tags = "业务工作台-客户视图-客户一览")
@RequestMapping(path = "/dashboard/client/overview")
public interface DashboardClientOverviewApi {
    @ApiOperation("业务工作台-客户视图-客户一览-统计")
    @PostMapping(path = "/statistics")
    R<List<DashboardClientOverviewStatisticsRSP>> statisticsList();

    @ApiOperation("业务工作台-客户视图-客户一览-所有客户明细")
    @PostMapping(path = "/all/list")
    R<Map<String, Object>> allPageList(@RequestBody @Valid DashboardClientOverviewAllREQ req);

    @ApiOperation("业务工作台-客户视图-客户一览-存续客户明细")
    @PostMapping(path = "/survival/list")
    R<Map<String, Object>> survivalPageList(@RequestBody @Valid DashboardClientOverviewSurvivalREQ req);

    @ApiOperation("业务工作台-客户视图-客户一览-3个月内结清客户明细")
    @PostMapping(path = "/settleinthreemonth/list")
    R<Map<String, Object>> settleInThreeMonthPageList(@RequestBody @Valid DashboardClientOverviewSettleInThreeMonthREQ req);

    @ApiOperation("业务工作台-客户视图-客户一览-逾期客户明细")
    @PostMapping(path = "/overdue/list")
    R<Map<String, Object>> overduePageList(@RequestBody @Valid DashboardClientOverviewOverdueREQ req);

    @ApiOperation("业务工作台-客户视图-客户一览-已结清客户明细")
    @PostMapping(path = "/settled/list")
    R<Map<String, Object>> settledPageList(@RequestBody @Valid DashboardClientOverviewSettledREQ req);
}
