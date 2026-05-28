package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 14:13
 */
@Data
public class GuaranteeDetail {
    @ApiModelProperty("担保机构id")
    private Long guaranteeAgencyId;

    @ApiModelProperty("担保机构Name")
    private Long guaranteeAgencyName;

    @ApiModelProperty("担保金额")
    private Long guaranteeAmount;

    @ApiModelProperty("剩余担保额度")
    private Long remainingGuaranteeAmount;
}
