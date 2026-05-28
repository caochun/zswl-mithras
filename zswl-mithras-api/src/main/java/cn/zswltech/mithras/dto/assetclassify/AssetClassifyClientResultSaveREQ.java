package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/1/8
 * @description
 */
@Data
@ApiModel("资产五级分类客户分类结果保存-请求体")
public class AssetClassifyClientResultSaveREQ {
    @NotNull(message = "id不能为空")
    @ApiModelProperty("资产五级分类客户信息id")
    private Long id;

    @NotBlank(message = "分类结果不能为空")
    @ApiModelProperty("分类结果")
    private String classifyResult;
}
