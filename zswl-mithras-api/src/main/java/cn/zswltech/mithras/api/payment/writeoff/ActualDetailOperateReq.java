package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 14:10
 */
@ApiModel("操作付款记录-入参")
@Data
public class ActualDetailOperateReq {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("是否已确认")
    private Integer isConfirmed;
}
