package cn.zswltech.mithras.dto.projestablish;

import cn.zswltech.mithras.annotation.MainIdExtract;
import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceModifyREQ;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceModifyREQ;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceModifyREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("修改报价方案复合请求体")
@MainIdExtract(expression = "aocPriceModifyREQ.projEstablishId,factoringPriceModifyREQ.projEstablishId,leasePriceModifyREQ.projEstablishId")
public class ProjEstablishPriceModifyREQ {
    @ApiModelProperty("修改报价方案债权转让请求体")
    private ProjEstablishAocPriceModifyREQ aocPriceModifyREQ;
    @ApiModelProperty("修改报价方案保理请求体")
    private ProjEstablishFactoringPriceModifyREQ factoringPriceModifyREQ;
    @ApiModelProperty("修改报价方案租赁请求体")
    private ProjEstablishLeasePriceModifyREQ leasePriceModifyREQ;
}
