package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-预算考核-投放计划执行情况表
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算考核-投放计划执行情况表-接口")
public interface BudgetExaminePayPlanExecuteApi {


    @ApiOperation("预算管理-预算考核-投放计划执行情况表列表")
    @PostMapping("/budget/examine/pay/plan/execute/list")
    R<List<BudgetExaminePayPlanExecuteListRSP>> list(@RequestBody @Valid BudgetExaminePayPlanExecuteListREQ req);

}