package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/9/19 19:26
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "修改业务流水请求参数")
public class UpdateBusinessFlowREQ extends WriteOffBaseREQ{

    @ApiModelProperty(value = "业务流水id")
    @NotNull(message = "业务流水id不能为空")
    private Long id;

    @ApiModelProperty(value = "TabId")
    @NotNull(message = "TabId不能为空")
    private Long financeFlowTabMainInfoId;

    @ApiModelProperty(value = "本次核销金额")
    @NotNull(message = "本次核销金额不能为空")
    private Long thisWriteOffAmount;
}
