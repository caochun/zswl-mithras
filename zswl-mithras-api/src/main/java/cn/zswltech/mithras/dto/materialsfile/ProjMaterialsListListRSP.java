package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-07-27
 **/
@Data
@ApiModel("项目资料清单列表-返回体")
public class ProjMaterialsListListRSP extends MaterialsListListRSP {
    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名")
    private String name;

    @ApiModelProperty("客户类型")
    private String clientType;

    @ApiModelProperty("展示类型名")
    private String clientTypeName;

    @ApiModelProperty("材料审核id")
    private Long projReviewMaterialId;

    @ApiModelProperty("审核意见")
    private String reviewComments;

    @ApiModelProperty("审核说明")
    private String reviewInstructions;
}
