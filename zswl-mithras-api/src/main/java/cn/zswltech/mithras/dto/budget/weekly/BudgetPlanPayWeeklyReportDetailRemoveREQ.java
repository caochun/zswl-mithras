package cn.zswltech.mithras.dto.budget.weekly;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 预算管理-投放计划（月度）-项目周报-详情
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-投放计划（月度）-项目周报-详情删除-请求体")
public class BudgetPlanPayWeeklyReportDetailRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
