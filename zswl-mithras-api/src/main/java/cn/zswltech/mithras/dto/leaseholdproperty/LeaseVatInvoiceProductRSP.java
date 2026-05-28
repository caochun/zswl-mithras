package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yupengfei
 * @date 2024/5/9 9:52
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVatInvoiceProductRSP {

    private Long id;

    @ApiModelProperty(value = "发票产品信息id")
    private Long invoiceId;

    @ApiModelProperty(value = "货物或服务名称")
    private String invoiceGoods;

    @ApiModelProperty(value = "规格型号")
    private String invoicePlateSpecific;

    @ApiModelProperty(value = "单位")
    private String invoiceElectransUnit;

    @ApiModelProperty(value = "数量")
    private String invoiceElectransQuantity;

    @ApiModelProperty(value = "税率")
    private String invoiceTaxRate;

    @ApiModelProperty(value = "税额")
    private String invoiceTax;

    @ApiModelProperty(value = "金额（含税）")
    private String invoicePrice;

    @ApiModelProperty(value = "金额（不含税）")
    private String taxNotIncluded;

    @ApiModelProperty(value = "车架号")
    private String vehicleInvoiceCarVin;
}
