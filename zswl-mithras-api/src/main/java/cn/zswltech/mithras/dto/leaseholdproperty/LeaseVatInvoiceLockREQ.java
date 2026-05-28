package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/14 15:47
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVatInvoiceLockREQ {

    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private List<Long> vatInvoiceIds;

    @ApiModelProperty(value = "锁定，解锁")
    private Boolean isLocked;
}
