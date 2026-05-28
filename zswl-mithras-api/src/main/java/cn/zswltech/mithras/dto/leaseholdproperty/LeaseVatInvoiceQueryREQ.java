package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/8 17:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVatInvoiceQueryREQ extends PageReq {

    @ApiModelProperty(value = "id")
    @NotNull(message = "租赁物id不能为空")
    private Long leaseholdId;

    @ApiModelProperty(value = "发票id")
    private List<Long> vatInvoiceIds;

    @ApiModelProperty(value = "购买方")
    private String invoicePayerName;

    @ApiModelProperty(value = "销售方")
    private String invoiceSellerName;

    @ApiModelProperty(value = "开票日期始")
    private LocalDate invoiceIssueDateFrom;

    @ApiModelProperty(value = "开票日期终")
    private LocalDate invoiceIssueDateTo;

    @ApiModelProperty(value = "验真结果")
    private String verifyResult;

    @ApiModelProperty(value = "发票状态")
    private String status;
}
