package cn.zswltech.mithras.service.controller.budget;

import cn.zswltech.mithras.api.budget.BudgetPlanCostDetailApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostDetailListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostDetailListRSP;
import cn.zswltech.mithras.service.service.budget.BudgetPlanCostService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-预算计划-成本预算-明细
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetPlanCostDetailController implements BudgetPlanCostDetailApi {
    @Resource
    private BudgetPlanCostService budgetPlanCostService;

    @Override
    public R<List<BudgetPlanCostDetailListRSP>> list(@Valid BudgetPlanCostDetailListREQ req) {
        return R.ok(budgetPlanCostService.detailList(req.getBudgetPlanCostId()));
    }
}