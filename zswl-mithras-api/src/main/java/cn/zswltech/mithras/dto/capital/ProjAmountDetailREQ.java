package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author yangxiong
 * @date 2024/5/30/09:47
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProjAmountDetailREQ extends CashFlowCodeListREQ{

    @ApiModelProperty(value = "现金流编号")
    @NotBlank(message = "现金流编号不能为空")
    private String cashFlowCode;
}
