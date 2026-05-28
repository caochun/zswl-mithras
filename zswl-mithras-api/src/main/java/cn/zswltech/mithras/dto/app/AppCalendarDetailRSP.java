package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author yibin
 */
@Data
public class AppCalendarDetailRSP {

    @ApiModelProperty("收款id")
    private Long collectionId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("计划收款日期")
    private LocalDate planCollectionDate;

    @ApiModelProperty(value = "当前期数/总期数")
    private String repayTimesRate;

    @ApiModelProperty(value = "应付租金")
    private Long planCollectionAmount;

    @ApiModelProperty(value = "是否逾期（第二天）")
    private Boolean isOverDue;

    @ApiModelProperty(value = "剩余未收租金")
    private Long unCollectionAmount;

    @ApiModelProperty(value = "应付罚息金额")
    private Long penaltyInterest;

    @ApiModelProperty(value = "罚息减免金额")
    private Long penaltyInterestDeductionAmount;

    @ApiModelProperty(value = "已收罚息金额")
    private Long collectionPenaltyInterest;

    @ApiModelProperty(value = "未收罚息金额")
    private Long unCollectionPenaltyInterest;
}
