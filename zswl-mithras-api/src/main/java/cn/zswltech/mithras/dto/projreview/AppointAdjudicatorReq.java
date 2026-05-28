package cn.zswltech.mithras.dto.projreview;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/30 15:09
 */
@ApiModel("指定定价终审人-入参")
@Data
public class AppointAdjudicatorReq {
    @ApiModelProperty("流程Id")
    @NotNull
    private String processInstanceId;

    @ApiModelProperty("终审人枚举value")
    @NotNull
    private String adjudicator;
}
