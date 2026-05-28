package cn.zswltech.mithras.dto.afterlease.rentcollection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2022-11-18
 **/
@Data
public class OverdueRentListInfoRSP {

    @ApiModelProperty("借据id")
    private Long paymentId;

    @ApiModelProperty("借据code")
    private String paymentCode;

    @ApiModelProperty("催收层级")
    private String collectionLevel;

    @ApiModelProperty("通知财务系统收款 true可通知")
    private Boolean noticeFinancialCollection;

    @ApiModelProperty("逾期记录")
    private List<OverdueRent> overdueRents;

    @Data
    public static class OverdueRent {
        @ApiModelProperty("期项")
        private Integer phase;

        @ApiModelProperty("计划收款日期")
        private LocalDate planCollectionDate;

        @ApiModelProperty("本金")
        private Long principal;

        @ApiModelProperty("利息")
        private Long interest;

        @ApiModelProperty("罚息")
        private Long penaltyInterest;

        @ApiModelProperty("是否逾期")
        private Boolean overdue;

        @ApiModelProperty("逾期天数")
        private Long overdueDay;

        @ApiModelProperty("核销状态")
        private String writeOffStatus;
    }
}
