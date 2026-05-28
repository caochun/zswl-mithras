package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yupengfei
 * @date 2024/5/9 11:32
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVatInvoiceCountRSP {

    @ApiModelProperty(value = "本次识别合计")
    private Long total;

    @ApiModelProperty(value = "识别成功张数")
    private Long succeed;

    @ApiModelProperty(value = "验真通过张数")
    private Long pass;

    @ApiModelProperty(value = "验真不通过")
    private Long noPass;

    @ApiModelProperty(value = "识别失败张数")
    private Long fail;

    @ApiModelProperty(value = "支持对内容进行锁定")
    private Boolean canLock;
}
