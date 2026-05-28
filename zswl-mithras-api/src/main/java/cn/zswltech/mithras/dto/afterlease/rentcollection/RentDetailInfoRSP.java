package cn.zswltech.mithras.dto.afterlease.rentcollection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2022-11-17
 **/

@Data
public class RentDetailInfoRSP {

    @ApiModelProperty(value = "收款核销id")
    private Long id;

    @ApiModelProperty(value = "收款核销code")
    private String collectionCode;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("金额合计")
    private Long totalAmount;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty(value = "计划收款日期")
    private LocalDate planCollectionDate;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;

    @ApiModelProperty(value = "罚息减免金额")
    private Long creditAmount;

    @ApiModelProperty("实收金额合计")
    private Long collectionAmount;

    @ApiModelProperty("收款记录明细")
    private List<Records> collections;

    @Data
    public static class Records {
        @ApiModelProperty("记录id")
        private Long id;

        @ApiModelProperty("实收日期")
        private LocalDate collectionDate;

        @ApiModelProperty("收款方式")
        private String collectionType;

        @ApiModelProperty("本金")
        private Long principal;

        @ApiModelProperty("利息")
        private Long interest;

        @ApiModelProperty("罚息")
        private Long penaltyInterest;
    }
}
