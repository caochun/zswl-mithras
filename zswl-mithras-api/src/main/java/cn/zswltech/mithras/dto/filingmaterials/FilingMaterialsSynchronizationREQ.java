package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author lllin
 * @date 2025-12-03
 */
@Data
public class FilingMaterialsSynchronizationREQ extends FilingBaseREQ{
    @ApiModelProperty("承租人id")
    private Long clientId;
    @NotNull
    @ApiModelProperty("同步业务类型")
    private String businessType;
}
