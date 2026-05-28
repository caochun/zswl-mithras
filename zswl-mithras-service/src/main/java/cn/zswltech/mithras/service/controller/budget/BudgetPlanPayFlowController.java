package cn.zswltech.mithras.service.controller.budget;

import cn.zswltech.mithras.api.budget.BudgetPlanPayFlowApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayFlowService;
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
    private BudgetPlanPayFlowService budgetPlanPayFlowService;

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