package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/7/8/15:42
 * @description
 */
@Data
@ApiModel(value = "资金端付款的手动核销")
public class FinancePaymentWriteOffREQ {

    /**
     * 流水列表ID
     */
    @ApiModelProperty(value = "流水ID列表")
    @NotEmpty(message = "流水ID列表不能为空")
    private List<Long> financeFlowIds;

    /**
     * 数据列表json
     */
    @ApiModelProperty(value = "数据列表json")
    private String listDataJson;

    @ApiModelProperty(value = "是否是自动核销, 0, 1")
    private Integer isAuto;

    @ApiModelProperty(value = "还款计划拆分后的核销明细（目前仅ABS和ABN类型的业务会传此参数）")
    private List<RepaySplitInfo> repaySplitInfoList;

    @Data
    public static class RepaySplitInfo {
        @ApiModelProperty("还款计划未拆分的现金流编号")
        private String cashFlowCode;
        @ApiModelProperty("还款计划拆分后的明细id")
        private Long repaySplitId;
        @ApiModelProperty("还款计划拆分后的明细编号")
        private String repaySplitCashFlowCode;
        @ApiModelProperty("现金流类型，用于区分本金还是利息")
        private String cashFlowItem;
        @ApiModelProperty("核销金额")
        private Long writeOffAmount;
    }
}
