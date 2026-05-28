package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @create: 2022-07-27
 **/
@Data
@ApiModel("归档项目资料清单列表-返回体")
public class FilingProjMaterialsListListRSP{
    @NotNull
    @ApiModelProperty("tab页code")
    private String tabCode;

    @NotNull
    @ApiModelProperty("所属模块")
    private String moduleCode;

    @NotNull
    @ApiModelProperty("tab下展示模块")
    private List<FilingProjMaterialsDetailRSP> projMaterialsListListRSP;
}
