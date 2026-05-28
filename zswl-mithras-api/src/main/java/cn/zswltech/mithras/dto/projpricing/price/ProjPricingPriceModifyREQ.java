package cn.zswltech.mithras.dto.projpricing.price;

import cn.zswltech.mithras.annotation.MainIdExtract;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("项目评审报价方案更新-请求体")
@MainIdExtract(expression = "aocPriceModifyREQ.projectId,factoringPriceModifyREQ.projectId,leasePriceModifyREQ.projectId")
public class ProjPricingPriceModifyREQ {
    @ApiModelProperty("租赁报价方案请求体")
    private ProjPricingLeasePriceModifyREQ leasePriceModifyREQ;
    @ApiModelProperty("保理报价方案请求体")
    private ProjPricingFactoringPriceModifyREQ factoringPriceModifyREQ;
    @ApiModelProperty("债权转让报价方案请求体")
    private ProjPricingAocPriceModifyREQ aocPriceModifyREQ;

    public Long getProjectId() {
        if (aocPriceModifyREQ != null) {
            return aocPriceModifyREQ.getProjectId();
        }
        if (factoringPriceModifyREQ != null) {
            return factoringPriceModifyREQ.getProjectId();
        }
        if (leasePriceModifyREQ != null) {
            return leasePriceModifyREQ.getProjectId();
        }
        return null;
    }

    public Integer getIrr() {
        if (aocPriceModifyREQ != null) {
            return aocPriceModifyREQ.getIrrPercent();
        }
        if (factoringPriceModifyREQ != null) {
            return factoringPriceModifyREQ.getIrrPercent();
        }
        if (leasePriceModifyREQ != null) {
            return leasePriceModifyREQ.getIrrPercent();
        }
        return null;
    }

    public Long getAmount() {
        if (aocPriceModifyREQ != null) {
            return aocPriceModifyREQ.getApplyCreditAmount();
        }
        if (factoringPriceModifyREQ != null) {
            return factoringPriceModifyREQ.getApplyCreditAmount();
        }
        if (leasePriceModifyREQ != null) {
            return leasePriceModifyREQ.getApplyCreditAmount();
        }
        return null;
    }
}
