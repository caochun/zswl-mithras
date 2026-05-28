package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 付款模块资料清单
 *
 * @author wangchuanhao
 * @date 2022/9/23 5:23 PM
 */
@Data
public class PaymentMaterialListREQ {
    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("付款id")
    private Long paymentId;
}
