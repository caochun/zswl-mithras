package cn.zswltech.mithras.dto.fund.financing.repay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("融资管理-还款实际表导入-返回体")
public class FundFinancingRepayActualImportRSP {
    @ApiModelProperty("利息差额")
    private Long interestDiff;

}
