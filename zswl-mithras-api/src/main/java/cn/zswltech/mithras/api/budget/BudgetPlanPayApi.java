package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayListRSP;

/**
* @description 预算管理-预算计划-投放计划
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算计划-投放计划-接口")
public interface BudgetPlanPayApi {
    @ApiOperation("预算管理-预算计划-投放计划-列表")
    @PostMapping("/budget/plan/pay/pageList")
    R<PageR<BudgetPlanPayListRSP>> pageList(@RequestBody @Valid BudgetPlanPayListREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-信息")
    @PostMapping("/budget/plan/pay/info")
    R<BudgetPlanPayRSP> planInfo(@RequestBody @Valid SinglePkREQ req);
}