package cn.zswltech.mithras.dto.budget;

import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;

/**
 * @description 预算管理-预算计划-投放计划
 * @author vico
 * @date 2025-04-11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("预算管理-预算计划-投放计划列表-请求体")
public class BudgetPlanPayListREQ extends PageReq {

}
