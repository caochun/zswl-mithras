package cn.zswltech.mithras.dto.projestablish;

import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("报价方案复合查询列表-返回体")
public class ProjEstablishPriceDetailRSP {

    @ApiModelProperty(value = "租赁报价方案")
    private ProjEstablishLeasePriceRSP leasePriceRSP;

    @ApiModelProperty(value = "债权转让报价方案")
    private ProjEstablishAocPriceRSP aocPriceRSP;

    @ApiModelProperty(value = "保理报价方案")
    private ProjEstablishFactoringPriceRSP factoringPriceRSP;

    public Long getDeclaredAmount() {
        if(aocPriceRSP != null){
            return aocPriceRSP.getApplyCreditAmount();
        }
        if(factoringPriceRSP != null){
            return factoringPriceRSP.getApplyCreditAmount();
        }
        if(leasePriceRSP != null){
            return leasePriceRSP.getApplyCreditAmount();
        }
        return null;
    }
}
