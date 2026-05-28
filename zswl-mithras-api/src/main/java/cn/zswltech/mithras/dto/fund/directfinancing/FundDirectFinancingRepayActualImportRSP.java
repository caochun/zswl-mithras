package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("直接融资-导入实际还款表-返回体")
public class FundDirectFinancingRepayActualImportRSP {

    @ApiModelProperty(value = "利息差额")
    private Long interestDiff;
}
