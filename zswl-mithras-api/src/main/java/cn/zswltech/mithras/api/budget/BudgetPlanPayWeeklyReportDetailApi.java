package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.weekly.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-投放计划（月度）-项目周报-详情
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-投放计划（月度）-项目周报-详情-接口")
public interface BudgetPlanPayWeeklyReportDetailApi {

    /*@ApiOperation("修改预算管理-投放计划（月度）-项目周报-详情")
    @PostMapping("/budget/plan/pay/weekly/report/detail/modify")
    R<Void> modify(@RequestBody @Valid BudgetPlanPayWeeklyReportDetailModifyREQ req);

    @ApiOperation("预算管理-投放计划（月度）-项目周报-详情列表")
    @PostMapping("/budget/plan/pay/weekly/report/detail/list")
    R<PageR<BudgetPlanPayWeeklyReportDetailListRSP>> list(@RequestBody @Valid BudgetPlanPayWeeklyReportDetailListREQ req);
*/
    @ApiOperation("预算管理-投放计划（月度）-项目周报-明细-统计")
    @PostMapping(path = "/budget/plan/weekly/report/detail/statistics")
    R<BudgetPlanPayWeeklyReportDetailStatisticsRSP> statisticsMonth(@RequestBody @Valid BudgetPlanPayWeeklyReportDetailListREQ req);

    @ApiOperation("预算管理-投放计划（月度）-项目周报-明细-列表")
    @PostMapping(path = "/budget/plan/weekly/report/pageList")
    R<PageR<BudgetPlanPayWeeklyReportDetailListRSP>> pageListMonth(@RequestBody @Valid BudgetPlanPayWeeklyReportDetailListREQ req);

    @ApiOperation("预算管理-投放计划（月度）-项目周报-明细-新增")
    @PostMapping(path = "/budget/plan/weekly/report/detail/add")
    R<Void> addDetailMonth(@RequestBody @Valid BudgetPlanPayWeeklyReportDetailAddREQ req);

    @ApiOperation("预算管理-投放计划（月度）-项目周报-明细-编辑")
    @PostMapping(path = "/budget/plan/weekly/report/detail/modify")
    R<Void> modifyDetailMonth(@RequestBody @Valid BudgetPlanPayWeekReportDetailModifyREQ req);

    @ApiOperation("预算管理-投放计划（月度）-项目周报-明细-列表-合同信息")
    @PostMapping(path = "/budget/plan/weekly/detail/pageList/contract")
    R<List<BudgetPlanPayWeeklyReportDetailListRSP>> listMonthDetailContract(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-投放计划（月度）-项目周报-明细-部门确认情况")
    @PostMapping(path = "/budget/plan/weekly/detail/deptConfirm/list")
    R<List<BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP>> listBizDeptConfirmInfo(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-投放计划（月度）-项目周报-明细-删除")
    @PostMapping(path = "/budget/plan/pay/weekly/report/detail/batchDelete")
    R<Void> batchDelete(@RequestBody @Valid MultiplePkREQ req);



}