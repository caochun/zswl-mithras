package cn.zswltech.mithras.dto.report.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 征信报送-账户表编辑入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:16 PM
 */
@Data
@ApiModel("征信报送-账户表编辑入参")
public class AccountModifyREQ {

    @NotNull(message = "修改原因不能为空")
    @ApiModelProperty("修改原因")
    private String reason;

    @ApiModelProperty("id")
    @NotNull
    private Long id;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("业务标识")
    private String businessKey;

    @ApiModelProperty("租金计算方式")
    private String rentalCalcType;

    @ApiModelProperty("还款频率")
    private String repayRate;

    @ApiModelProperty("借款金额")
    private Long paymentAmount;

    @ApiModelProperty("保证金")
    private Long earnestMoney;

    @ApiModelProperty("借款期限(月)")
    private Integer projLeaseMonthCount;

    @ApiModelProperty("放款日期")
    private LocalDate lendingDate;

    @ApiModelProperty("结清日期")
    private LocalDate closedDate;

}
