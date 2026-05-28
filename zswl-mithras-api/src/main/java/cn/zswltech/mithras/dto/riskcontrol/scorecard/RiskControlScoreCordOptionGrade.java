package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName RiskControlScoreCordAreaType
 * @Description 分区打分
 * @Author jackerhe
 * @Date 2023/3/2 3:47 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCordOptionGrade {
    @ApiModelProperty(value = "优良")
    private Long good;
    @ApiModelProperty(value = "中等")
    private Long moderate;
    @ApiModelProperty(value = "较差")
    private Long bad;

}
