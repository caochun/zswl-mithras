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
public class FilingMaterialsQueryREQ extends FilingBaseREQ{
    @NotNull
    @ApiModelProperty("tab页code")
    private String tabCode;
    @NotNull
    @ApiModelProperty("模块code")
    private String moduleCode;
}
