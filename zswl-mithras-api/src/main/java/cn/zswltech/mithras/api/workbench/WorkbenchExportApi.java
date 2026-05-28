package cn.zswltech.mithras.api.workbench;

import cn.zswltech.mithras.dto.workbench.CardListExportReq;
import cn.zswltech.mithras.dto.workbench.ProjectMetricExportReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2024/4/2
 * @description
 */
@Api(tags = "工作台相关导出接口")
public interface WorkbenchExportApi {
    @ApiOperation("工作台-本年新增投放客户列表-导出")
    @PostMapping("/workbench/chart/list/year/client/export")
    void clientListExport(@RequestBody @Valid CardListExportReq req) throws Exception;

    @ApiOperation("工作台-本年/本月新增立项列表-导出")
    @PostMapping("/workbench/chart/list/year/proj/export")
    void projListExport(@RequestBody @Valid CardListExportReq req) throws Exception;

    @ApiOperation("工作台-本年新增项目评审列表-导出")
    @PostMapping("/workbench/chart/list/year/review/export")
    void reviewList(@RequestBody @Valid CardListExportReq req) throws Exception;

    @ApiOperation("工作台-本年/本月累计投放金额列表-导出")
    @PostMapping("/workbench/chart/list/year/payment/export")
    void paymentList(@RequestBody @Valid CardListExportReq req) throws Exception;

    @ApiOperation("工作台-逾期项目列表-导出")
    @PostMapping("/workbench/chart/list/overdue/proj/export")
    void overdueList(@RequestBody @Valid CardListExportReq req) throws Exception;

    @ApiOperation("工作台-不良项目列表-导出")
    @PostMapping("/workbench/chart/list/undesirable/proj/export")
    void undesirableList(@RequestBody @Valid CardListExportReq req) throws Exception;

    @ApiOperation("工作台-大可视化新增评审表格-导出")
    @PostMapping("/workbench/chart/list/review/export")
    void reviewList(@RequestBody @Valid ProjectMetricExportReq req) throws Exception;

    @ApiOperation("工作台-大可视化新增投放表格-导出")
    @PostMapping("/workbench/chart/list/launch/export")
    void launchList(@RequestBody @Valid ProjectMetricExportReq req) throws Exception;
}
