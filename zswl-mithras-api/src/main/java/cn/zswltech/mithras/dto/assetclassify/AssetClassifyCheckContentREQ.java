package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/8 1:46 下午
 **/
@Data
@ApiModel("风控管理-资产分类/总结-返回体")
public class AssetClassifyCheckContentREQ extends VersionBaseREQ {

    @ApiModelProperty("详情id")
    @NotNull(message = "详情id不能为空")
    private Long  assetClassifyClientId;

    //ProcessModelTypeEnum
    @ApiModelProperty("流程类型")
    private String processType;
}
