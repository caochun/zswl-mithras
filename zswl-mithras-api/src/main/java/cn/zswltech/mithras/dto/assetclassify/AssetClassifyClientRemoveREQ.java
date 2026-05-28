package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("资产分类删除客户详情-请求体")
public class AssetClassifyClientRemoveREQ {
    @NotNull(message = "id不能为空")
    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "季度分类id为空")
    @ApiModelProperty("季度分类id")
    private Long assetClassifyId;
}
