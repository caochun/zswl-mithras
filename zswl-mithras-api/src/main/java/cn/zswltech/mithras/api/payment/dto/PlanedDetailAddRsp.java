package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 10:23
 */
@ApiModel("添加计划付款明细-出参")
@Data
public class PlanedDetailAddRsp {
    @ApiModelProperty("id")
    private Long id;
}
