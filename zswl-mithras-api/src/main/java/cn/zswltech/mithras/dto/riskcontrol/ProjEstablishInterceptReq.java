package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/15 14:18
 */
@ApiModel("立项拦截请求体")
@Data
public class ProjEstablishInterceptReq {
    @ApiModelProperty("立项id")
    @NotNull(message = "立项id不能为空")
    private Long projEstablishId;
}
