package cn.zswltech.mithras.api.metric;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.metric.factor.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @author yibin
 */
@Api(tags = "指标报表导入接口")
@RestController
public interface MetricFactorApi {

    @ApiOperation("明细列表")
    @PostMapping("/risk/metric/factor/detail/pagelist")
    R<PageR<RiskMetricFactorPageListRsp>> detailPageList(@RequestBody @Valid RiskMetricFactorPageListReq req);

    @ApiOperation("风险指标文件-删除")
    @PostMapping("/risk/metric/factor/file/remove")
    R<Boolean> removeFile(@RequestBody @Valid RiskMetricFactorFileRemoveReq req);

    @ApiOperation("风险指标-文件列表")
    @PostMapping("/risk/metric/factor/file/list")
    R<PageR<RiskMetricFactorFileListRsp>> fileList(@RequestBody @Valid RiskMetricFactorFileListReq req);


    @ApiOperation("导入报表excel")
    @PostMapping("/risk/metric/factor/import")
    R<Void> importFactors(@RequestParam("file") MultipartFile file);


    @ApiOperation("风险指标因子-列表")
    @PostMapping("/risk/metric/factor/list")
    R<PageR<RiskMetricFactorListRsp>> list(@RequestBody @Valid RiskMetricFactorListReq req);


    @ApiOperation("报表删除")
    @PostMapping("/risk/metric/factor/remove")
    R<Void> removeFactors(@RequestBody @Valid RiskMetricFactorImportRemoveReq req);

    @ApiOperation("刷新表")
    @PostMapping("/risk/metric/factor/refresh")
    R<Void> refresh(@RequestBody @Valid RiskMetricFactorRefreshReq req);

    @PostMapping("/risk/metric/factor/test")
    void test();
}
