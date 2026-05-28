package cn.zswltech.mithras.dto.capital;

import cn.zswltech.mithras.dto.collection.BillManagementAddREQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/6/3
 * @description
 */
@Data
public class BusinessFlowFinanceDetailSaveREQ {
    @NotNull(message = "收付款id不能为空")
    @ApiModelProperty("资金收付款id")
    private Long receiptRepayId;

    @NotBlank(message = "现金流类型不能为空")
    @ApiModelProperty("现金流类型")
    private String cashFlowItem;

    @NotBlank(message = "现金流编号不能为空")
    @ApiModelProperty("现金流编号")
    private String cashFlowCode;

    @NotBlank(message = "核销日期不能为空")
    @ApiModelProperty("核销日期")
    private String cashFlowDate;

//    @NotNull(message = "核销金额不能为空")
    @ApiModelProperty("核销金额")
    private Long totalAmount;

    @ApiModelProperty("本金金额")
    private Long principalAmount;

    @ApiModelProperty("利息金额")
    private Long interestAmount;

    @NotBlank(message = "结算方式不能为空")
    @ApiModelProperty("结算方式")
    private String settleMethod;

    @ApiModelProperty("银行流水编号")
    private String bankDetailNo;

    @ApiModelProperty("银行流水id")
    private Long financeFlowId;

    @ApiModelProperty("我们的银行账号")
    private String ourAccountNumber;

    @ApiModelProperty("我们的银行开户行")
    private String ourAccountBank;

    @ApiModelProperty("我们的银行账户")
    private String ourAccountName;

    @ApiModelProperty("来源")
    private String dataSource;

    @ApiModelProperty("票据信息")
    private BillManagementAddREQ billManagementAddREQ;
}
