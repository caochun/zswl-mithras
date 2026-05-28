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
public class FilingMaterialsGroupQueryRSP{
    @NotNull
    @ApiModelProperty("列表code")
    private String groupCode;
    @NotNull
    @ApiModelProperty("列表name")
    private String groupName;
    @NotNull
    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("列表层级分组数据")
    private List<Pair<String, List<FileListRSP>>> levelFileList;

    @ApiModelProperty("列表非层级分组数据")
    private List<FileListRSP> nonLevelfileListR;
}
