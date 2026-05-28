package cn.zswltech.mithras.dto.projestablish;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel(value = "立项生效-请求体")
public class ProjEstablishEffectREQ {
    @NotNull
    @ApiModelProperty("立项Id")
    public Long id;
}
