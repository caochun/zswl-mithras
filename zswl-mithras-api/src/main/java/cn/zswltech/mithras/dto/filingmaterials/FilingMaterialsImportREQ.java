package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author lllin
 * @date 2025-12-03
 */
@Data
public class FilingMaterialsImportREQ extends FilingBaseREQ{
    @ApiModelProperty("承租人id")
    private Long clientId;
    @NotNull
    @ApiModelProperty("业务类型")
    private String businessType;
    @NotNull
    @ApiModelProperty("资料类型")
    private String materialsType;
    @NotNull
    @ApiModelProperty("资料id")
    private List<Long> materialIds;
}
