package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2024/6/3
 * @description
 */
@Data
public class BusinessFlowFinanceDetailListREQ {
    @ApiModelProperty("现金流编号")
    @NotBlank(message = "现金流编号不能为空")
    private String cashFlowCode;
}
