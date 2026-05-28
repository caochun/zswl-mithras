package cn.zswltech.mithras.dto.projestablish;

import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceAddREQ;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceAddREQ;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceAddREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("新增报价方案复合请求体")
public class ProjEstablishPriceAddREQ {
    @ApiModelProperty("新增报价方案债券转让请求体")
    private ProjEstablishAocPriceAddREQ aocPriceAddREQ;
    @ApiModelProperty("新增报价方案保理请求体")
    private ProjEstablishFactoringPriceAddREQ factoringPriceAddREQ;
    @ApiModelProperty("新增报价方案租赁请求体")
    private ProjEstablishLeasePriceAddREQ leasePriceAddREQ;
}
