package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AssetClassifyResidueWorkdayRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/1/8 4:19 下午
 * @Version 1.0
 **/
@ApiModel("五级分类-剩余工作日返回体")
@Data
public class AssetClassifyResidueWorkdayRSP {

    @ApiModelProperty("剩余工作天数")
    private Integer residueWorkday;

}
