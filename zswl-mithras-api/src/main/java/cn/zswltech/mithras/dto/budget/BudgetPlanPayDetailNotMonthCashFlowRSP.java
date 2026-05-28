package cn.zswltech.mithras.dto.budget;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayDetailNotMonthCashFlowRSP {
    @ApiModelProperty("日期")
    private LocalDate cashFlowDate;

    @ApiModelProperty("期项")
    private Integer cashFlowPhase;

    @ApiModelProperty("现金流金额")
    private Long cashFlowAmount;

    @ApiModelProperty("租金")
    private Long rent;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("剩余本金")
    private Long remainingPrincipal;
}
