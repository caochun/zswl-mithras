package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 10:08
 */
@Data
@ApiModel("付款材料操作-请求体")
public class PaymentMaterialsOperateReq {
    @NotNull(message = "材料记录id不能为空")
    @ApiModelProperty("材料记录id")
    private Long id;
}