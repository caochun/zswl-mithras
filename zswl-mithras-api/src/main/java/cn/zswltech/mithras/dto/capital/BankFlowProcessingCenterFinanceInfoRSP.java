package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class BankFlowProcessingCenterFinanceInfoRSP {
    @ApiModelProperty("收付款id")
    private Long receiptRepayBaseId;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资金额")
    private Long financingAmount;

    @ApiModelProperty("实际贷款日期")
    private String actualLoanDate;

    @ApiModelProperty("业务类型")
    private String businessType;
}
