package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;

import java.util.List;

/**
* @description 预算管理-预算计划-利润预算
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算计划-利润预算-接口")
public interface BudgetPlanProfitApi {

    @ApiOperation("预算管理-预算计划-利润预算-创建预算")
    @PostMapping("/budget/plan/profit/create")
    R<Long> create(@RequestBody @Valid BudgetPlanProfitCreateREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-列表")
    @PostMapping("/budget/plan/profit/pageList")
    R<PageR<BudgetPlanProfitListRSP>> list(@RequestBody @Valid BudgetPlanProfitListREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-信息")
    @PostMapping("/budget/plan/profit/info")
    R<BudgetPlanProfitRSP> info(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-删除")
    @PostMapping("/budget/plan/profit/remove")
    R<Void> remove(@RequestBody @Valid BudgetPlanProfitRemoveREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-汇总")
    @PostMapping("/budget/plan/profit/summary")
    R<BudgetPlanProfitSummaryRSP> summary(@RequestBody @Valid BudgetPlanProfitSummaryREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-汇总（其他类型）")
    @PostMapping("/budget/plan/profit/summaryOther")
    R<List<BudgetPlanProfitSummaryOtherRSP>> summaryOther(@RequestBody @Valid BudgetPlanProfitSummaryOtherREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-预算确认")
    @PostMapping("/budget/plan/profit/confirm")
    R<Void> confirm(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-投放进度")
    @PostMapping("/budget/plan/profit/process")
    R<List<BudgetPlanProfitProcessRSP>> process(@RequestBody @Valid BudgetPlanProfitProcessREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-预算明细-存量")
    @PostMapping("/budget/plan/profit/detail/history")
    R<List<BudgetPlanProfitDetailHistoryRSP>> detailHistory(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-预算明细-新增")
    @PostMapping("/budget/plan/profit/detail/feature")
    R<List<BudgetPlanProfitDetailFutureRSP>> detailFuture(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-发送待办（启动投放计划收集流程）")
    @PostMapping("/budget/plan/profit/detail/notifyCreatePlanPay")
    R<Void> notifyCreatePlanPay(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-预算明细-部门详情")
    @PostMapping("/budget/plan/profit/detail/dept")
    R<List<BudgetPlanProfitDetailRSP>> deptDetail(@RequestBody @Valid BudgetPlanProfitDetailREQ req);

    @ApiOperation("预算管理-预算计划-利润预算-预算明细-部门详情-编辑")
    @PostMapping("/budget/plan/profit/detail/dept/modify")
    R<Void> modifyProfitDetail(@RequestBody @Valid BudgetPlanProfitDetailModifyREQ req);
}