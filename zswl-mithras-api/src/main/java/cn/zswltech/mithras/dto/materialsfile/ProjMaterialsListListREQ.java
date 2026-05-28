package cn.zswltech.mithras.dto.materialsfile;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @create: 2022-07-27
 **/
@Data
@ApiModel("项目资料清单列表-请求体")
public class ProjMaterialsListListREQ extends VersionBaseREQ {
    @NotNull
    @ApiModelProperty("projId")
    private Long projId;
}
