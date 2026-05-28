package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "业务工作台-运营部-时效统计和详情")
@RequestMapping(path = "/dashboard/operation/time")
public interface DashboardOperationTimeApi {


    @ApiOperation("业务工作台-运营部-时效统计-平均耗时(工作日)")
    @PostMapping(path = "/statistics")
    R<List<DashboardOperationTimeStatisticsRSP>> timeStatistics(@RequestBody @Valid DashboardOperationTimeStatisticsREQ req);


    @ApiOperation("业务工作台-运营部-时效统计-时间周期")
    @PostMapping(path = "/term")
    R<List<DashboardOperationTimePercentageRSP>> timeTerm(@RequestBody @Valid DashboardOperationTimeStatisticsREQ req);

    @ApiOperation("业务工作台-运营视角-时效统计-详情")
    @PostMapping(path = "/list")
    R<Map<String,Object>> detailList(@RequestBody @Valid DashboardOperationTimeListREQ req);
}
