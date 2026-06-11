package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@ApiModel("实际利率法-列表-参数")
@EqualsAndHashCode(callSuper = true)
public class MonthlyRPListRSP extends TabBaseRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("借据id")
    private Long receiptId;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("租赁类型")
    private String leaseType;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("税率")
    private Long taxRate;

    @ApiModelProperty("本月收入金额（含税）")
    private Long incomeSum;

    @ApiModelProperty("本月收入金额（不含税）")
    private Long incomeWithoutTaxSum;

    @ApiModelProperty("当前是否逾期")
    private String overdueType;

    @ApiModelProperty("月份")
    private String yearAndMonth;

    @ApiModelProperty("实际起租日")
    private LocalDate actualLeaseDate;

    @ApiModelProperty("最近一期全额偿还租金期次")
    private Integer theLatestFullRefundRentPeriod;

    @ApiModelProperty("最近一期全额偿还租金应收日期")
    private LocalDate theLatestFullRefundRentDate;

    @ApiModelProperty("最近一期全额偿还租金应收剩余本金")
    private Long theLatestFullRefundRentCapital;

    @ApiModelProperty("合同名义利率")
    private Integer contractNominalInterestRate;
}
