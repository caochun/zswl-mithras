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


@Api(tags = "业务工作台-运营视角-产能分析")
@RequestMapping(path = "/dashboard/operation/capacity")
public interface DashboardOperationCapacityApi {

    @ApiOperation("业务工作台-运营视角-详情")
    @PostMapping(path = "/list")
    R<Map<String,Object>> list(@RequestBody @Valid DashboardOperationCapacityListREQ req);

    @ApiOperation("业务工作台-运营视角-产能分析-部门产能分析")
    @PostMapping(path = "/statistics")
    R<List<DashboardOperationCapacityStatisticsRSP>> statistics(@RequestBody @Valid DashboardOperationCapacityStatisticsREQ req);


}
