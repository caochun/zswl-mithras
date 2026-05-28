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
public class RematchTabREQ extends WriteOffBaseREQ {

    @ApiModelProperty(value = "当前tab的ID")
    @NotNull(message = "当前tab的ID不能为空")
    private Long financeFlowTabMainInfoId;

    @ApiModelProperty(value = "提前N天")
    @NotNull(message = "提前N天不能为空")
    private Integer plusDays;
}
