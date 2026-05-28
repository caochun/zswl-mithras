package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/22 14:16
 */
@Data
@ApiModel("担保明细编辑-请求体")
public class FundCreditGuaranteeDetailDto {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("所属授信id")
    private Long creditId;

    @ApiModelProperty("担保机构id")
    private Long guaranteeAgencyId;
    @ApiModelProperty("担保机构Name")
    private String guaranteeAgencyName;

    @ApiModelProperty("担保金额")
    private Long guaranteeAmount;

    @ApiModelProperty("剩余担保金额")
    private Long remainingLimit;
}
