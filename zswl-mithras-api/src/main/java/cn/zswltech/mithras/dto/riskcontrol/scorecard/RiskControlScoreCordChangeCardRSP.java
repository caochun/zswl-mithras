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
public class RiskControlScoreCordChangeCardRSP {

    @ApiModelProperty(value = "地区id")
    private Long areaId;

    @ApiModelProperty(value = "评分卡id")
    private Long cardId;

    /*@ApiModelProperty(value = "地区")
    private String area;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "行政级别")
    private String executiveLevel;

    @ApiModelProperty(value = "区域级别")
    private String regionalLevel;

    @ApiModelProperty(value = "总分")
    private BigDecimal totalPoints;
*/
}
