package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SingleTabREQ extends WriteOffBaseREQ {

    @ApiModelProperty(value = "当前tab的ID")
    @NotNull(message = "当前tab的ID不能为空")
    private Long financeFlowTabMainInfoId;
}
