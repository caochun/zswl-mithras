package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingClientAccessCheckREQ {

    @ApiModelProperty(value = "客户id")
//    @NotNull(message = "客户id不得为空")
    private Long clientId;

    @ApiModelProperty(value = "模型编号")
    @NotNull(message = "模型编号不得为空")
    private String code;

    @ApiModelProperty(value = "模型名称")
    @NotNull(message = "模型名称不得为空")
    private String name;

    @ApiModelProperty(value = "客户名称")
//    @NotNull(message = "客户名称不得为空")
    private String clientName;

}
