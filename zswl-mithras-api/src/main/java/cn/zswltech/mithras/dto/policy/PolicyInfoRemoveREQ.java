package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("保单维护删除-请求体")
public class PolicyInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
