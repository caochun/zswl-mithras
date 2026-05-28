package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName AssetClassifyNodeRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/1/4 3:48 下午
 * @Version 1.0
 **/
@ApiModel("五级分类-定级流程区请求体")
@Data
public class AssetClassifyNodeREQ {

    @ApiModelProperty("主表id")
    @NotNull(message = "分类id不能为空")
    private Long assetClassifyId;

}
