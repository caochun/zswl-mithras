package cn.zswltech.mithras.budget.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.budget.application.BudgetPlanPayApplicationService;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayRSP;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

import cn.zswltech.mithras.api.budget.BudgetPlanPayApi;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayListRSP;

/**
* @description 预算管理-预算计划-投放计划
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetPlanPayController implements BudgetPlanPayApi {
    @Resource
    private BudgetPlanPayApplicationService budgetPlanPayService;

    @Override
    public R<PageR<BudgetPlanPayListRSP>> pageList(BudgetPlanPayListREQ req) {
        return R.ok(budgetPlanPayService.pageList(req));
    }

    @Override
    public R<BudgetPlanPayRSP> planInfo(SinglePkREQ req) {
        return R.ok(budgetPlanPayService.planInfo(req.getId()));
    }
}
