package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.OperationEfficiencyDetailListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.OperationEfficiencyStatisticsListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.OperationEfficiencyStatisticsListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/17:28
 * @description
 */
@Api(tags = "管理工作台-项目运营效率接口")
@RequestMapping(path = "/dashboard")
public interface DashboardProjectOperationEfficiencyApi {

    @PostMapping(path = "/operationefficiency/statistics")
    @ApiOperation(value = "项目运营效率统计")
    R<List<OperationEfficiencyStatisticsListRSP>> getEfficiencyStatisticsList(@RequestBody OperationEfficiencyStatisticsListREQ req);

    @PostMapping(path = "/operationefficiency/list")
    @ApiOperation(value = "项目运营效率明细")
    R<List<OperationEfficiencyDetailListRSP>> list(@RequestBody OperationEfficiencyStatisticsListREQ req);
}
