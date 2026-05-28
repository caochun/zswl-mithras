package cn.zswltech.mithras.api.workbench;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.*;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/9 14:12
 */
@Api(tags = "工作台-指标图-接口")
public interface WorkbenchChartMetricApi {

    @ApiOperation("工作台-当前角色table列表")
    @PostMapping("/workbench/chart/tabs")
    R<List<String>> tabs(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-柱状图指标")
    @PostMapping("/workbench/chart/bar")
    R<LineBarChartValueVO> barChart(@RequestBody @Valid WorkbenchBarMetricReq req);

    @ApiOperation("工作台-资金流动性分析曲线图")
    @PostMapping("/workbench/chart/fundsliquidity")
    R<LineBarChartValueVO> fundsLiquidityChart(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-项目整体收益率曲线图（部门对比）")
    @PostMapping("/workbench/chart/dept/returnrate")
    R<LineBarChartValueVO> deptReturnRateChart(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-项目整体收益率曲线图（行业对比）")
    @PostMapping("/workbench/chart/projecttype/returnrate")
    R<LineBarChartValueVO> projectTypeReturnRateChart(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-各行业存量本金柱状图")
    @PostMapping("/workbench/chart/stockprincipal")
    R<LineBarChartValueVO> stockPrincipalChart(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-项目投放/回款情况曲线图")
    @PostMapping("/workbench/chart/releasecollection")
    R<LineBarChartValueVO> releaseCollectionChart(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-大可视化新增评审表格")
    @PostMapping("/workbench/chart/list/review")
    R<List<ProjectVO>> reviewList(@RequestBody @Valid ProjectMetricReq req);

    @ApiOperation("工作台-大可视化新增投放表格")
    @PostMapping("/workbench/chart/list/launch")
    R<PageR<PaymentDetailRsp>> launchList(@RequestBody @Valid ProjectMetricReq req);

    @ApiOperation("工作台-本年新增投放客户列表")
    @PostMapping("/workbench/chart/list/year/client")
    R<PageR<ClientProjectListRSP>> clientList(@RequestBody @Valid CardListReq req);

    @ApiOperation("工作台-本年/本月新增立项列表")
    @PostMapping("/workbench/chart/list/year/proj")
    R<PageR<NewProjectListRSP>> projList(@RequestBody @Valid CardListReq req);

    @ApiOperation("工作台-本年新增项目评审列表")
    @PostMapping("/workbench/chart/list/year/review")
    R<PageR<NewReviewListRSP>> reviewList(@RequestBody @Valid CardListReq req);

    @ApiOperation("工作台-本年/本月累计投放金额列表")
    @PostMapping("/workbench/chart/list/year/payment")
    R<PageR<NewPaymentListRSP>> paymentList(@RequestBody @Valid CardListReq req);

    @ApiOperation("工作台-逾期项目列表")
    @PostMapping("/workbench/chart/list/overdue/proj")
    R<PageR<ProjInfoListRSP>> overdueList(@RequestBody @Valid CardListReq req);

    @ApiOperation("工作台-不良项目列表")
    @PostMapping("/workbench/chart/list/undesirable/proj")
    R<PageR<ProjInfoListRSP>> undesirableList(@RequestBody @Valid CardListReq req);

}
