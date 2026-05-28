package cn.zswltech.mithras.dto.fund.financing.repay;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("融资管理-还款概算表列表-返回体")
public class FundFinancingRepayEstimateListRSP extends ListBaseRSP {
    @ApiModelProperty("日期")
    private String repayDate;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("本金")
    private Long principleAmount;

    @ApiModelProperty("利息")
    private Long interestAmount;

    @ApiModelProperty("应还金额")
    private Long repayAmount;

    @ApiModelProperty("剩余未还本金")
    private Long remainingPrincipleAmount;
}
