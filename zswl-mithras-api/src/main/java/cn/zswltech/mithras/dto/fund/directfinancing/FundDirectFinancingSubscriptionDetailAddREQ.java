package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 直接融资-认购明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-认购明细新增-请求体")
public class FundDirectFinancingSubscriptionDetailAddREQ {

    @ApiModelProperty(value = "融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;

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

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}
