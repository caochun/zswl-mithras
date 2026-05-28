package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/5/19/11:32
 * @description
 */
@Data
public class BankFlowProcessingCenterFinanceDetailREQ {

    @ApiModelProperty(value = "核销类型")
    private String writeOffType;

    @ApiModelProperty(value = "现金流ID")
    @NotNull(message = "现金流ID不能为空")
    private Long cashFlowId;

    @ApiModelProperty(value = "期项")
    @NotNull(message = "期项不能为空")
    private Integer phase;

    @ApiModelProperty(value = "现金流类型")
    @NotBlank(message = "现金流类型不能为空")
    private String cashFlowType;

}
