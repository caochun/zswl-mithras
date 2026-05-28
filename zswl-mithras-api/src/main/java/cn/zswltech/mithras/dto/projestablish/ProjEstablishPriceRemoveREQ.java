package cn.zswltech.mithras.dto.projestablish;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("删除报价方案复合请求体")
public class ProjEstablishPriceRemoveREQ {
    @ApiModelProperty("删除报价方案租赁方案ID")
    private Long leasePriceId;
    @ApiModelProperty("删除报价方案债券转让方案ID")
    private Long aocPriceId;
    @ApiModelProperty("删除报价方案保理方案ID")
    private Long factoringPriceId;
}
