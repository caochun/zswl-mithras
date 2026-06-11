package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class BankFlowProcessingCenterFinancePaymentCashFlowSumRSP implements Serializable {

    private static final long serialVersionUID = 8154665058295891793L;
    @ApiModelProperty(value = "流水金额合计(元)")
    private Long cashFlowAmountSum;

    @ApiModelProperty("应付金额合计(元)")
    private Long shouldPayAmountSum;

    @ApiModelProperty("未付款金额(元)")
    private Long noPayAmountSum;

    @ApiModelProperty("列表")
    private List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> list;

    @ApiModelProperty("融资剩余本金")
    private List<RemainingDetail> remainingDetailList;


    @Data
    public static class RemainingDetail{
        private Long financingId;

        private String financingType;

        private Long remainingAmount;
    }

}
