package cn.zswltech.mithras.api.budget;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 预算管理-预算计划-成本预算
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算计划-成本预算-接口")
public interface BudgetPlanCostApi {

    @ApiOperation("预算管理-预算计划-成本预算列表")
    @PostMapping("/budget/plan/cost/list")
    R<PageR<BudgetPlanCostListRSP>> list(@RequestBody @Valid BudgetPlanCostListREQ req);

}