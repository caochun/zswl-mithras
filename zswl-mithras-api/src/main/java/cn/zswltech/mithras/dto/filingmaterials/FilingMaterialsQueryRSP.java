package cn.zswltech.mithras.dto.filingmaterials;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author lllin
 * @date 2025-12-03
 */
@Data
public class FilingMaterialsQueryRSP{
    @NotNull
    @ApiModelProperty("tab页code")
    private String tabCode;
    @NotNull
    @ApiModelProperty("模块code")
    private String moduleCode;
    @NotNull
    @ApiModelProperty("文件列表")
    private List<FilingMaterialsGroupQueryRSP> filingMaterialsGroupQueryRSPList;
}
