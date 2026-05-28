package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 16:31
 */
@ApiModel("删除计划付款明细-入参")
@Data
public class PlanedDetailRemoveReq {
    @ApiModelProperty("id")
    @NotNull(message = "id 为 null")
    private Long id;
}
