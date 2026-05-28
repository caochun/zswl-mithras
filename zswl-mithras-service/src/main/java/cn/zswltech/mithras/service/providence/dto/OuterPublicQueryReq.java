package cn.zswltech.mithras.service.providence.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/31 10:17
 */
@Data
@ApiModel("外部查询请求体")
public class OuterPublicQueryReq{

    @ApiModelProperty("查询主id")
    @NotNull
    private Long publicInfoQueryId;

}
