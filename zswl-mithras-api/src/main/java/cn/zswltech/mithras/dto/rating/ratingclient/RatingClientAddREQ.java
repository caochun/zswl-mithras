package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingClientAddREQ {

    @ApiModelProperty(value = "客户id")
//    @NotNull(message = "客户id不得为空")  // 航运模型进来有可能客户id为空
    private Long clientId;

    @ApiModelProperty("模型code")
    @NotNull(message = "模型code不得为空")
    private String code;

    @ApiModelProperty("模型名称")
    @NotNull(message = "模型名称不得为空")
    private String name;

//    @ApiModelProperty("评级id，更新评级时传递")
//    private Long id;

    @ApiModelProperty("客户名称")
//    @NotNull(message = "客户名称不得为空")
    private String clientName;
}
