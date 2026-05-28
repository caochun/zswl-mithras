package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostDetailListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostDetailListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-预算计划-成本预算-明细
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算计划-成本预算-明细-接口")
public interface BudgetPlanCostDetailApi {
    @ApiOperation("预算管理-预算计划-成本预算-明细列表")
    @PostMapping("/budget/plan/cost/detail/list")
    R<List<BudgetPlanCostDetailListRSP>> list(@RequestBody @Valid BudgetPlanCostDetailListREQ req);
}