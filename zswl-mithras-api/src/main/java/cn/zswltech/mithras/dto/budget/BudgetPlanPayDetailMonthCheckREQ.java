package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class BudgetPlanPayDetailMonthCheckREQ {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("拟投放金额")
    private Long planPayAmount;

}
