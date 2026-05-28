package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author vico
 * @description 预算管理-预算计划-投放计划
 * @date 2025-04-11
 */
@Api(tags = "预算管理-预算计划-流程-接口")
public interface BudgetPlanPayFlowApi {

    @ApiOperation("预算管理-预算计划-创建-信息-年度")
    @PostMapping("/budget/plan/pay/flow/add")
    R addYearFlow(@RequestBody @Valid SinglePkREQ req);


    @ApiOperation("预算管理-预算计划-创建-信息-月度")
    @PostMapping("/budget/plan/pay/flow/Month/add")
    R addMonthFlow(@RequestBody @Valid SinglePkREQ req);
}