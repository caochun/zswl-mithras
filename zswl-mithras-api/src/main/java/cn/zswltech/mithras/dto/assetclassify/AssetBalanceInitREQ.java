package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@ApiModel("五级分类-手动初始化资产余额")
@Data
public class AssetBalanceInitREQ {

    @ApiModelProperty("年份")
    @NotNull(message = "年份不能为空")
    private Integer year;

    @ApiModelProperty("季度")
    @NotNull(message = "季度不能为空")
    private Integer quarter;

}
