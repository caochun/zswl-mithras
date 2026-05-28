package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AreaSearchRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/2 4:03 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCordAreaSearchREQ {

    @ApiModelProperty(value = "类型 TitleNameEnum")
    private String areaType;

    @ApiModelProperty(value = "地区名称")
    private String areaName;

    @ApiModelProperty(value = "年份")
   // @NotNull(message = "年份不能为空")
    private Integer year;

}
