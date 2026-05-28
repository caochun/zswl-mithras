package cn.zswltech.mithras.dto.payment.lib;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description payment_policy_info
 * @author zhaozhengkang
 * @date 2022-09-13
 */
@Data
@ApiModel("payment_policy_info列表-请求体")
public class PaymentPolicyInfoListREQ extends PageReq {

    private Long paymentId;

    @ApiModelProperty(value = "已到期或5天内将到期标识")
    private Boolean adventFlag;

}
