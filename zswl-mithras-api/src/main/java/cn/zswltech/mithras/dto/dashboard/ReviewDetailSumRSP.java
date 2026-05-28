package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lizhao
 * @date 2024/7/02
 * @description
 */
@Data
public class ReviewDetailSumRSP {
    @ApiModelProperty("评审阶段列表")
    private List<DashboardProjectStageReviewDetailRSP> list;
    @ApiModelProperty("合计")
    private ReviewDetailSumRSP.SumData sumData;

    @Data
    public static class SumData{
        @ApiModelProperty("授信金额合计")
        private BigDecimal creditAmount;
        @ApiModelProperty("合同金额合计")
        private BigDecimal contractAmount;
        @ApiModelProperty("申请付款金额合计")
        private BigDecimal applyPayAmount;
        @ApiModelProperty("已收租金合计")
        private BigDecimal totalCollectionRent;
        @ApiModelProperty("剩余金额")
        private BigDecimal totalRentBalance;
        @ApiModelProperty("存量风险敞口合计")
        private BigDecimal stockRiskExposure;
    }
}
