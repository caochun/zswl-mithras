package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 直接融资-认购明细
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-认购明细编辑-请求体")
public class FundDirectFinancingSubscriptionDetailModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 认购机构
    */
    @ApiModelProperty(value = "认购机构")
    private Long orgnizationId;

    /**
     * 认购机构name
     */
    @ApiModelProperty(value = "认购机构名称")
    private String orgnizationName;

    /**
    * 认购证券
    */
    @ApiModelProperty(value = "认购证券")
    private Long productId;

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
