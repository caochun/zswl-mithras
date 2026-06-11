package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("保单维护提交-请求体")
public class PolicyInfoSubmitREQ {

    @ApiModelProperty("保单父id")
    @NotNull(message = "保单id不得为空")
    private Long parentId;
}
