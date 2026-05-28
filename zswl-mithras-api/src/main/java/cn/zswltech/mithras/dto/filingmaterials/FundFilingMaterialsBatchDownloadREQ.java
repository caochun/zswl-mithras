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
public class FundFilingMaterialsBatchDownloadREQ extends FilingBaseREQ{
    @NotNull
    @ApiModelProperty("模块code")
    private String moduleCode;

    @ApiModelProperty("文件id")
    private List<Long> fileIds;
}
