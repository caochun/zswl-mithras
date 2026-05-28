package cn.zswltech.mithras.dto.fund.financing.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/2/27
 * @description
 */
@Data
@ApiModel("融资管理-融资起息信息-返回体")
public class FundFinancingCarryInterestInfoRSP {
    @ApiModelProperty("还款日")
    private Integer repayDay;

    @ApiModelProperty("实际贷款日期")
    private String actualLoanDate;
}
