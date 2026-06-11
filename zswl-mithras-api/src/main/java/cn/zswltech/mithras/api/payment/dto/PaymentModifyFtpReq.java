package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@Data
@ApiModel("付款申请-修改FTP成本-请求体")
public class PaymentModifyFtpReq {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "基础价格")
    private Long basePrice;

    @ApiModelProperty(value = "山区调整")
    private Long mountainAdjustment;

    @ApiModelProperty(value = "评级调整")
    private Long gradeAdjustment;

    @ApiModelProperty(value = "FTP是否质押")
    private Long pledgePrice;

    @ApiModelProperty(value = "手工调整")
    private Long handAdjustment;

    @ApiModelProperty(value = "票据价格")
    private Long ticketPrice;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "杭甬特殊调整")
    private Long hangyongSpecialAdjustment;
}
