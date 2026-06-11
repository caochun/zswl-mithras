package cn.zswltech.mithras.budget.controller;
import cn.zswltech.mithras.api.budget.BudgetExaminePayPlanExecuteApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteListRSP;
import cn.zswltech.mithras.budget.application.BudgetExaminePayPlanExecuteApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 预算管理-预算考核-投放计划执行情况表
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetExaminePayPlanExecuteController implements BudgetExaminePayPlanExecuteApi {

    @Resource
    private BudgetExaminePayPlanExecuteApplicationService budgetExaminePayPlanExecuteService;

    @Override
    public R<List<BudgetExaminePayPlanExecuteListRSP>> list(BudgetExaminePayPlanExecuteListREQ req){
        return R.ok(budgetExaminePayPlanExecuteService.list(req));
    }


}