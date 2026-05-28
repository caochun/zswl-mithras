package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/9 10:12
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVatInvoiceRemoveREQ {

    @ApiModelProperty(value = "租赁物清单id")
    private Long leaseholdId;

    @ApiModelProperty(value = "操作类型(REPLACE:替换发票)")
    private String operateType;

    @ApiModelProperty(value = "id", required = true)
    private List<Long> vatInvoiceIds;
}
