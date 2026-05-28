package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectInfoRentThisMonthRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("现金流编号")
    private String cashFlowCode;
    @ApiModelProperty("期项")
    private Integer phase;
    @ApiModelProperty("本期租金")
    private ValueUnitDTO rent;
    @ApiModelProperty("本期本金")
    private ValueUnitDTO principalAmount;
    @ApiModelProperty("本期利息")
    private ValueUnitDTO interestAmount;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO principalBalanceAmount;
    @ApiModelProperty("剩余利息")
    private ValueUnitDTO interestBalanceAmount;
    @ApiModelProperty("剩余金额")
    private ValueUnitDTO balanceAmount;
    @ApiModelProperty("本期应收日期")
    private LocalDate planCollectionDate;
    @ApiModelProperty("实际还款日期")
    private LocalDate actualCollectionDate;
    @ApiModelProperty("是否逾期")
    private Integer isOverdue;
    @ApiModelProperty("法务催收记录")
    private List<UrgeCollectionRecord> urgeCollectionRecords;
    @ApiModelProperty("已收租金")
    private ValueUnitDTO collectionRentAmount;
    @ApiModelProperty("已收本金")
    private ValueUnitDTO collectionPrincipalAmount;
    @ApiModelProperty("已收利息")
    private ValueUnitDTO collectionInterestAmount;
}
