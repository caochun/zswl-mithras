package cn.zswltech.mithras.dto.filingmaterials;

import cn.zswltech.mithras.dto.materialsfile.ProjMaterialsListListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-07-27
 **/
@Data
@ApiModel("归档项目资料清单列表-返回体")
public class FilingProjMaterialsDetailRSP extends ProjMaterialsListListRSP{
    @ApiModelProperty("客户一级目录枚举")
    private String custDirEnum;
}
