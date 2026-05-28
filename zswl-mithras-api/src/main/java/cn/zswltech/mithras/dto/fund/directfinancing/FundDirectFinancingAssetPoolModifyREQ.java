package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-资产池信息
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-资产池信息编辑-请求体")
public class FundDirectFinancingAssetPoolModifyREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "封包日")
    @NotNull(message = "封包日不能为空")
    private LocalDate packageDate;

    @ApiModelProperty(value = "加权平均贷款年利率（%）")
    @NotNull(message = "加权平均贷款年利率不能为空")
    private Long averageAnnualInterestRate;

    @ApiModelProperty(value = "期末贷款笔数")
    private Long numberOfLoans;

    @ApiModelProperty(value = "加权平均合同期限（月）")
    private Long averageContractTerm;

    @ApiModelProperty(value = "借款人户数（户）")
    private Long numberOfBorrowers;

    @ApiModelProperty(value = "加权平均账龄（月）")
    private Long averageAging;

    @ApiModelProperty(value = "最高贷款利率（%）")
    private Long maxLoanInterestRate;

    @ApiModelProperty(value = "最低贷款利率（%）")
    private String minLoanInterestRate;

    @ApiModelProperty(value = "期末租金余额（万元）")
    private Long endingRentBalance;

    @ApiModelProperty(value = "期末本金余额（万元）")
    private Long endingPrincipalBalance;

    @ApiModelProperty(value = "加权平均剩余期限（月）")
    private Long averageRemainingTerm;

    @ApiModelProperty(value = "覆盖倍数")
    private Long coverageMultiple;

    @ApiModelProperty(value = "单笔贷款最高本金余额（万元）")
    private Long maxPrincipalBalance;

    @ApiModelProperty(value = "单笔贷款平均本金余额（万元）")
    private Long averagePrincipalBalance;

    @ApiModelProperty(value = "贷款最长剩余期限（月）")
    private Long maxRemainingTerm;

    @ApiModelProperty(value = "贷款最短剩余期限（月）")
    private Long minRemainingTerm;

}
