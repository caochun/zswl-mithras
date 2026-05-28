package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/6/27/16:16
 * @description
 */
@Api(tags = "绩效考核-业绩目标管理")
@RequestMapping(path = "/kpi/performance/manage")
public interface KpiPerformanceManageApi {

    @ApiOperation(value = "业绩主列表")
    @PostMapping(path = "/main/list")
    R<PageR<KpiPerformanceManageMainListRSP>> mainList(@RequestBody @Valid KpiPerformanceManageMainListREQ req);

    @ApiOperation(value = "业绩主列表详情")
    @PostMapping(path = "/main/detail")
    R<KpiPerformanceManageMainDetailRSP> mainDetail(@RequestBody @Valid KpiPerformanceManageMainDetailREQ req);

    @ApiOperation(value = "业绩新增")
    @PostMapping(path = "/add")
    R<Void> add(@RequestBody @Valid KpiPerformanceManageAddREQ req);

    @ApiOperation(value = "业绩目标导出")
    @PostMapping(path = "/export")
    void export(@RequestBody @Valid KpiPerformanceManageExportREQ req);

    @ApiOperation(value = "业绩目标列表")
    @PostMapping(path = "/list")
    R<KpiPerformanceManageListRSP> list(@RequestBody @Valid KpiPerformanceManageListREQ req);

    @ApiOperation(value = "业绩目标导入")
    @PostMapping(path = "/import")
    R<Void> importKpiPerformance(@Valid KpiPerformanceManageImportREQ req);

    @ApiOperation(value = "业绩目标状态修改")
    @PostMapping(path = "/modify")
    R<Void> modifyStatus(@RequestBody @Valid KpiPerformanceManageModifyREQ req);

}
