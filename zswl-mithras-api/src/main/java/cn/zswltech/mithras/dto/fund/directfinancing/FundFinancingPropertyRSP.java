package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资/间接融资-投放资产明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资/间接融资-投放资产明细详情-返回体")
public class FundFinancingPropertyRSP {
    @ApiModelProperty(value = "融资Id")
    private String financingId;

    @ApiModelProperty(value = "直接融资Id")
    private String directId;

    @ApiModelProperty(value = "间接融资Id")
    private String finanId;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资机构")
    private String financingOrg;

    @ApiModelProperty("融资金额")
    private BigDecimal financingAmount;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("业务类型名称")
    private String bizTypeName;

    @ApiModelProperty("合同金额")
    private BigDecimal contractAmount;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("银行账号")
    private String accountNumber;

    @ApiModelProperty("开户银行")
    private String accountBank;

    @ApiModelProperty("出款金额")
    private BigDecimal putoutAmount;

    @ApiModelProperty("出款日期")
    private LocalDate putoutDate;
}
