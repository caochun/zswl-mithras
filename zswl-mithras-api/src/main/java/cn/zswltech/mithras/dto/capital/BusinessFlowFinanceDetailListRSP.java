package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/3
 * @description
 */
@Data
public class BusinessFlowFinanceDetailListRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("来源")
    private String dataSource;

    @ApiModelProperty("总金额")
    private Long totalAmount;

    @ApiModelProperty("本金金额")
    private Long principalAmount;

    @ApiModelProperty("利息金额")
    private Long interestAmount;

    @ApiModelProperty("核销日期")
    private String cashFlowDate;

    @ApiModelProperty("银行流水号")
    private String bankFlowNo;

    @ApiModelProperty("结算方式")
    private String settleMethod;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作日期")
    private String operateDate;

    @ApiModelProperty(value = "票据code")
    private String billCode;

    @ApiModelProperty(value = "票据金额")
    private Long billAmount;

    @ApiModelProperty(value = "票据到期日期")
    private LocalDate billExpireDate;

    @ApiModelProperty("票据买入价")
    private Long billBuyRate;
}
