package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/8 1:46 下午
 **/
@Data
@ApiModel("风控管理-资产分类-检查内容/总结-返回体")
public class AssetClassifyCheckContentPackRSP {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("列表数据")
    private List<AssetClassifyCheckContentRSP> assetClassifyCheckContent;
}
