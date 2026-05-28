package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/9/5
 * @description
 */
@Data
public class AppraisalCompanyWhitelistModifyREQ {
    @NotNull(message = "id不能为空")
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "出库原因")
    private String outReason;
}
