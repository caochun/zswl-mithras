package cn.zswltech.mithras.budget.interfaces;

import cn.zswltech.mithras.api.budget.BudgetPlanPayFlowApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.budget.application.BudgetPlanPayFlowApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author vico
 * @description 预算管理-预算计划-投放计划
 * @date 2025-04-11
 */
@RestController
public class BudgetPlanPayFlowController implements BudgetPlanPayFlowApi {
    @Resource
    private BudgetPlanPayFlowApplicationService budgetPlanPayFlowService;

    @Override
    public R addYearFlow(SinglePkREQ req) {
        budgetPlanPayFlowService.createYearFlow(req.getId());
        return R.ok();
    }

    @Override
    public R addMonthFlow(SinglePkREQ req) {
        budgetPlanPayFlowService.createMonthFlow(req.getId());
        return R.ok();
    }
}