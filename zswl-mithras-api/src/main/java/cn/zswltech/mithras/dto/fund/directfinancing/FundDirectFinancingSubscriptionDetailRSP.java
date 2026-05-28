package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/18 14:18
 */
@Data
@ApiModel("直接融资-认购明细-返回体")
public class FundDirectFinancingSubscriptionDetailRSP {
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 认购机构
     */
    @ApiModelProperty(value = "认购机构")
    private Long orgnizationId;

    @ApiModelProperty(value = "认购机构名称")
    private String orgnizationName;

    /**
     * 认购证券
     */
    @ApiModelProperty(value = "认购证券")
    private Long productId;

    @ApiModelProperty(value = "认购证券名称")
    private String productName;

    /**
     * 认购额度（万元）
     */
    @ApiModelProperty(value = "认购额度（万元）")
    private Long subscriptionLimit;

    @ApiModelProperty(value = "是否授信机构")
    private Boolean isCreditOrgnization;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}
