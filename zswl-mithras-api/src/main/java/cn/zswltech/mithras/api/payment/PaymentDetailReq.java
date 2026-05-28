package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/23 11:09
 */
@ApiModel("付款详情-入参")
@Data
public class PaymentDetailReq extends VersionBaseREQ {
    @ApiModelProperty("付款申请ID")
    @NotNull(message = "id为空")
    private Long id;

}
