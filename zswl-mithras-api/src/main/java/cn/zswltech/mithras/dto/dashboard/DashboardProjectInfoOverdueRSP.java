package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectInfoOverdueRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("现金流编号")
    private String cashFlowCode;
    @ApiModelProperty("期项")
    private Integer phase;
    @ApiModelProperty("逾期时长")
    private ValueUnitDTO overdueDuration;
    @ApiModelProperty("逾期金额")
    private ValueUnitDTO overdueAmount;
    @ApiModelProperty("罚息日利率")
    private ValueUnitDTO interestPenaltyDailyRate;
    @ApiModelProperty("罚息金额")
    private ValueUnitDTO interestPenaltyAmount;
    @ApiModelProperty("罚息减免金额")
    private ValueUnitDTO interestPenaltyReduceAmount;
    @ApiModelProperty("催收记录")
    private List<UrgeCollectionRecord> urgeCollectionRecords;
}
