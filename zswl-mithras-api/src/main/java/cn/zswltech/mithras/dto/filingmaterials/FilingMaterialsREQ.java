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
public class FilingMaterialsREQ extends FilingBaseREQ{
    @NotNull
    @ApiModelProperty("tab页code")
    private String tabCode;
    @NotNull
    @ApiModelProperty("模块code")
    private String moduleCode;

    @ApiModelProperty("承租人id")
    private Long clientId;

    @ApiModelProperty("文件id")
    private List<Long> fileIds;
}
