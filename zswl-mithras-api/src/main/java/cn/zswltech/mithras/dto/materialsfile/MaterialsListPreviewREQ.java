package cn.zswltech.mithras.dto.materialsfile;

import cn.zswltech.mithras.dto.MaterialsListIdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@ApiModel("资料清单预览-请求体")
@Data
public class MaterialsListPreviewREQ extends MaterialsListIdType {
    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private Long id;


    /**
     * 之前的预览是是编辑区id和版本表的version组合作为参数，进行预览的
     * 现在有些情况是直接传版本表的id进来。所以区分下id类型
     * idType:1:编辑；2：版本表
     */
    @ApiModelProperty("id类型")
    private Integer idType = 1;
}
