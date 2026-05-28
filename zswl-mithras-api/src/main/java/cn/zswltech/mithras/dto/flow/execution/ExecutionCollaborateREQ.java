package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 协同
 *
 * @author wangchuanhao
 * @date 2022/7/28 2:43 PM
 */
@Data
public class ExecutionCollaborateREQ extends ExecutionTaskBaseREQ {

    @ApiModelProperty("协同用户id")
    @NotNull(message = "协同用户id不能为空")
    private Long collaborateUserId;

}
