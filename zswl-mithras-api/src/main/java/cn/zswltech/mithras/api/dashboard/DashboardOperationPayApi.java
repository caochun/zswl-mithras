package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPayStatisticsRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayStatisticsREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayStatisticsRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Api(tags = "业务工作台-运营视角-投放完成情况")
@RequestMapping(path = "/dashboard/operation/pay")
public interface DashboardOperationPayApi {

    @ApiOperation("业务工作台-运营视角-投放情况-详情")
    @PostMapping(path = "/list")
    R<Map<String,Object>> payList(@RequestBody @Valid DashboardOperationPayListREQ req);

    @ApiOperation("业务工作台-运营视角-投放情况")
    @PostMapping(path = "/statistics")
    R<List<DashboardOperationPayStatisticsRSP>> payStatistics(@RequestBody @Valid DashboardOperationPayStatisticsREQ req);


    @ApiOperation("业务工作台-导入")
    @PostMapping("/import")
    R<Void> importExcel(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType);
}
