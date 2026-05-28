package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @create: 2022-07-27
 **/
@Data
@ApiModel("项目评审资料评审-请求体")
public class ProjReviewMaterialCommentsREQ {
    @NotNull
    @ApiModelProperty("材料审核id")
    private Long id;

    @NotNull
    @ApiModelProperty("审核意见")
    private String reviewComments;

    @ApiModelProperty("审核说明")
    private String reviewInstructions;
}
