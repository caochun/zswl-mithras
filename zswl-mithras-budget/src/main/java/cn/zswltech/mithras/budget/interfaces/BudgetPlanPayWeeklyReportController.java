package cn.zswltech.mithras.budget.interfaces;
import cn.zswltech.mithras.api.budget.BudgetPlanPayWeeklyReportApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.weekly.*;
import cn.zswltech.mithras.budget.application.BudgetPlanPayWeeklyReportApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
* @description 预算管理-投放计划-项目周报
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetPlanPayWeeklyReportController implements BudgetPlanPayWeeklyReportApi {

    @Resource
    private BudgetPlanPayWeeklyReportApplicationService budgetPlanPayWeeklyReportService;

    @Override
    public R<PageR<BudgetPlanPayWeeklyReportListRSP>> list(BudgetPlanPayWeeklyReportListREQ req){
        return R.ok(budgetPlanPayWeeklyReportService.pageList(req));
    }

    @Override
    public R<BudgetPlanPayWeeklyReportListRSP> planInfo(@Valid SinglePkREQ req) {
        return R.ok(budgetPlanPayWeeklyReportService.planInfo(req.getId()));
    }

}
