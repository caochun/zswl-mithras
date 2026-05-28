package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 11:30
 */
@ApiModel("核销收款申请-入参")
@Data
public class CollectionWriteOffReq {
    @ApiModelProperty("收款申请- id")
    private Long id;

    @ApiModelProperty("实收金额")
    private Long collectionAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;
}
