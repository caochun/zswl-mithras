package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "业务工作台-运营部-转化率和详情")
@RequestMapping(path = "/dashboard/operation/conversion")
public interface DashboardOperationConversionApi {


    @ApiOperation("业务工作台-运营部-转化率-平均值")
    @PostMapping(path = "/statistics")
    R<List<DashboardOperationConversionStatisticsRSP>> timeStatistics(@RequestBody @Valid DashboardOperationConversionStatisticsREQ req);


    @ApiOperation("业务工作台-运营部-转化率-时间周期")
    @PostMapping(path = "/term")
    R<List<DashboardOperationConversionPercentageRSP>> timeTerm(@RequestBody @Valid DashboardOperationConversionStatisticsREQ req);

    @ApiOperation("业务工作台-运营视角-转化率-详情")
    @PostMapping(path = "/list")
    R<Map<String,Object>> detailList(@RequestBody @Valid DashboardOperationConversionListREQ req);
}
