package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author yupengfei
 * @date 2024/5/9 14:44
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVatInvoiceRetestREQ {

    @ApiModelProperty("发票id")
    private Long invoiceId;

    @ApiModelProperty(value = "发票类型")
    private String invoiceType;

    @ApiModelProperty(value = "发票号码")
    private String invoiceNo;

    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @ApiModelProperty(value = "开票日期")
    private LocalDate invoiceDate;

    @ApiModelProperty(value = "发票验证码")
    private String verifyCode;

    @ApiModelProperty(value = "发票金额")
    private String invoiceSum;
}
