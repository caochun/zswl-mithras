package cn.zswltech.mithras.dto.dashboard.boss;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/16:43
 * @description
 */
@Data
public class DistributionAssetsIndustryListRSP {

    @ApiModelProperty(value = "国标行业分类（一级分类）")
    private String name;

    @ApiModelProperty(value = "行业资产余额")
    private String value;

    @ApiModelProperty(value = "行业资产余额单位")
    private String unit;
}
