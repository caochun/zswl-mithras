package cn.zswltech.mithras.dto.projestablish.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@ApiModel("立项版本详情-入参")
@Data
public class ProjEstablishVersionDetailREQ {
    @ApiModelProperty("立项id")
    @NotNull
    private Long projEstablishId;

    @ApiModelProperty("版本号")
    @NotNull
    private String version;
}
