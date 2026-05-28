package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("自然人基本信息删除-请求体")
public class NormalBaseInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
