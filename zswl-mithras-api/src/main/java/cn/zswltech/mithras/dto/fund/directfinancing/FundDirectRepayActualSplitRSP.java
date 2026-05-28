package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/3/29
 * @description
 */
@Data
public class FundDirectRepayActualSplitRSP {
    @ApiModelProperty("证券代码")
    private String securitiesCode;
    @ApiModelProperty("证券简称")
    private String abbreviation;
    @ApiModelProperty("归属该产品下的现金流列表")
    private List<RepayData> cashFlowList;

    @Data
    public static class RepayData {
        @ApiModelProperty("现金流id")
        private Long id;
        @ApiModelProperty("现金流编号")
        private String cashFlowCode;
        @ApiModelProperty("期项")
        private Integer phase;
        @ApiModelProperty("还款日期")
        private String repayDate;
        @ApiModelProperty("应还总额（毫厘）")
        private Long repayAmount;
        @ApiModelProperty("本金（毫厘）")
        private Long principalAmount;
        @ApiModelProperty("利息（毫厘）")
        private Long interestAmount;
        @ApiModelProperty("剩余本金（毫厘）")
        private Long remainingPrincipalAmount;
        @ApiModelProperty("核销状态")
        private String writeOffStatus;
    }
}
