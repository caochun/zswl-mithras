package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author yupengfei
 * @date 2024/5/9 11:57
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseItemIdREQ {

    @ApiModelProperty(value = "租赁物id")
    @NotNull(message = "租赁物id不能为空")
    private Long leaseholdId;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;
}
