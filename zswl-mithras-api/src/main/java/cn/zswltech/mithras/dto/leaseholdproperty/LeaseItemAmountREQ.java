package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/9/26
 * @description
 */
@Data
public class LeaseItemAmountREQ {
    @NotNull(message = "id不能为空")
    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "租赁物总金额不能为空")
    @ApiModelProperty("租赁物总金额")
    private Long totalAmount;
}
