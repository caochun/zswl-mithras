package cn.zswltech.mithras.dto.fund.financing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Data
@ApiModel("融资管理-创建融资-请求体")
public class FundFinancingCreateREQ {
//    @NotNull(message = "授信id不能为空")
    @ApiModelProperty("授信id")
    private Long fundCreditId;

//    @NotNull(message = "融资金额不能为空")
    @ApiModelProperty("融资金额")
    private Long financingAmount;

    @NotEmpty(message = "业务类型不得为空")
    @ApiModelProperty("业务类型")
    private String businessType;
}
