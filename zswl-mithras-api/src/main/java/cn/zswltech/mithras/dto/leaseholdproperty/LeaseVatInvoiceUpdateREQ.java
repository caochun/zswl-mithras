package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/9 10:18
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVatInvoiceUpdateREQ {
    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private List<Long> vatInvoiceIds;

    @ApiModelProperty(value = "发票号码")
    private String invoiceNo;

    @ApiModelProperty(value = "开票日期")
    private LocalDate invoiceIssueDate;

    @ApiModelProperty(value = "购买方")
    private String invoicePayerName;

    @ApiModelProperty(value = "销售方")
    private String invoiceSellerName;

    @ApiModelProperty(value = "开票内容（货物或服务名称）")
    private String invoiceGoods;

    @ApiModelProperty(value = "规格型号")
    private String invoicePlateSpecific;

    @ApiModelProperty(value = "单位")
    private String invoiceElectransUnit;
}
