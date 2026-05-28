package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportListREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 预算管理-投放计划-项目周报
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-投放计划-项目周报-接口")
public interface BudgetPlanPayWeeklyReportApi {

    @ApiOperation("预算管理-投放计划-项目周报列表")
    @PostMapping("/budget/plan/pay/weekly/report/list")
    R<PageR<BudgetPlanPayWeeklyReportListRSP>> list(@RequestBody @Valid BudgetPlanPayWeeklyReportListREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-信息")
    @PostMapping("/budget/plan/pay/weekly/report/info")
    R<BudgetPlanPayWeeklyReportListRSP> planInfo(@RequestBody @Valid SinglePkREQ req);

}