package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/5/30/09:47
 * @description
 */
@Data
public class CashFlowCodeListREQ {

    @ApiModelProperty(value = "合同ID")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /**
     * 收付款类型枚举 name
     */
    @ApiModelProperty(value = "收付款类型")
    @NotBlank(message = "收款类型不能为空")
    private String writeOffType;

    /**
     * 收款/付款现金流项目枚举 name
     */
    @ApiModelProperty(value = "现金流项目枚举name")
    @NotBlank(message = "现金流项目不能为空")
    private String cashFlowItem;
}
