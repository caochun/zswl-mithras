package cn.zswltech.mithras.dto.fund.financing.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Data
@ApiModel("融资管理-融资起息-请求体")
public class FundFinancingEffectREQ {
    @NotNull(message = "融资id不能为空")
    @ApiModelProperty("融资id")
    private Long financingId;

    @NotNull(message = "还款日不能为空")
    @ApiModelProperty("还款日")
    @Min(value = 1, message = "还款日需大于0")
    @Max(value = 31, message = "还款日需小于31")
    private Integer repaymentDate;

    @NotNull(message = "实际贷款时间不能为空")
    @ApiModelProperty("实际贷款时间")
    private LocalDate actualLoanDate;
}
