package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 14:07
 */
@ApiModel("添加付款记录-出参")
@Data
public class ActualDetailAddRsp {
    @ApiModelProperty("主键id")
    private Long id;
}
