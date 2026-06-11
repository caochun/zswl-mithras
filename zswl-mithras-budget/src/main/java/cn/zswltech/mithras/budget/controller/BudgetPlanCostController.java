package cn.zswltech.mithras.budget.controller;
import cn.zswltech.mithras.api.budget.BudgetPlanCostApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListRSP;
import cn.zswltech.mithras.budget.application.BudgetPlanCostApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 预算管理-预算计划-成本预算
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetPlanCostController implements BudgetPlanCostApi {

    @Resource
    private BudgetPlanCostApplicationService budgetPlanCostService;

    @Override
    public R<PageR<BudgetPlanCostListRSP>> list(BudgetPlanCostListREQ req){
        return R.ok(budgetPlanCostService.pageList(req));
    }

}
