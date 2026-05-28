package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/9/27 16:56
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeleteTabREQ extends WriteOffBaseREQ {

    @ApiModelProperty(value = "当前tab的ID")
    @NotNull(message = "当前tab的financeFlowTabMainInfoId不能为空")
    private Long financeFlowTabMainInfoId;
}
