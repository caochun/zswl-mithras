package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/5/7
 * @description
 */
@Data
public class BudgetPlanPayDetailMonthModifyREQ {
    @NotNull(message = "id不能为空")
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("租赁类型")
    private String leaseType;

    @ApiModelProperty("拟投放金额")
    private Long planPayAmount;

    @ApiModelProperty("投放日")
    private LocalDate planPayDate;

    @ApiModelProperty("期限")
    private Integer termMonth;

    @ApiModelProperty("项目保证金")
    private Long deposit;

    @ApiModelProperty("咨询服务费")
    private Long consultingFee;

    @ApiModelProperty("合同利率")
    private Integer contractInterestRate;

    @ApiModelProperty("irr")
    private Integer irr;

    @ApiModelProperty("ftp")
    private Integer ftp;

    @ApiModelProperty("本周项目计划节点")
    private String planActionThisWeek;

    @ApiModelProperty("本周项目实际节点")
    private String actualActionThisWeek;

    @ApiModelProperty("下周项目计划节点")
    private String planActionNextWeek;

    @ApiModelProperty("项目进展状态")
    private String projectProgress;

    @ApiModelProperty("系统流程阶段")
    private String rzyProcessStage;

    @ApiModelProperty("运营进度反馈")
    private String yunyingFeedback;

    @ApiModelProperty("运营优先级")
    private Integer yunyingPriority;

    @ApiModelProperty("是否纳入资金计划")
    private String bringIntoFundPlan;

    @ApiModelProperty("意见及反馈")
    private String suggestion;

    @ApiModelProperty("是否3A评级")
    private String ARate;

    @ApiModelProperty("管理层级")
    private String manageLevel;

    @ApiModelProperty("资金拟投放金额")
    private Long fundPlanPayAmount;
}
