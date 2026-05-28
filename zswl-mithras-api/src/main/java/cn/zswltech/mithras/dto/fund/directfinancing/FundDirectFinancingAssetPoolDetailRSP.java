package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/17 14:34
 */
@Data
@ApiModel("直接融资-资产池信息详情-返回体")
public class FundDirectFinancingAssetPoolDetailRSP {
    @ApiModelProperty(value = "id")
    private Long id;
    
    @ApiModelProperty(value = "融资id")
    private Long financingId;

    @ApiModelProperty(value = "封包日")
    private LocalDate packageDate;

    @ApiModelProperty(value = "加权平均贷款年利率（%）")
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
