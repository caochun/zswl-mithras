package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/5/19/11:23
 * @description
 */
@Data
public class BankFlowProcessingCenterPhaseListREQ {

    @ApiModelProperty(value = "借据ID")
    @NotNull(message = "借据ID不能为空")
    private Long receiptId;

    /**
     * 收款/付款核销顺序枚举 name
     */
    @ApiModelProperty(value = "现金流项目")
    private String cashFlowItem;

    /**
     * 收付款类型枚举 name
     */
    @ApiModelProperty(value = "付款或者收款枚举")
    @NotBlank(message = "付款或者收款类型不能为空")
    private String cashFlowType;

}
