package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/20 09:55
 */
@ApiModel("授信额度返回体")
@Data
public class CreditLimitRsp {
    @ApiModelProperty("原授信额度")
    private Long originalCreditLimit;

    @ApiModelProperty("已融资金额")
    private Long financingAmount;

    @ApiModelProperty("剩余授信金额")
    private Long remainingCreditLimit;
}
