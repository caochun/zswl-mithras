package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName AreaSearchRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/2 4:03 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCordTryCalculateRSP {

    @ApiModelProperty(value = "地区id")
    private Long areaId;

    @ApiModelProperty(value = "评分卡id")
    private Long cardId;

    @ApiModelProperty(value = "地区")
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

    private List<TryCalculateBody> tryCalculateBodies;


    @Data
    public class TryCalculateBody{

        private Long targetId;

        @ApiModelProperty(value = "指标名称")
        private String targetName;

        @ApiModelProperty(value = "指标权重")
        private Long targetWeight;

        @ApiModelProperty(value = "打分类型")
        private String gradeType;

        @ApiModelProperty(value = "选项记分")
        private RiskControlScoreCordOptionGrade optionGrade;

        @ApiModelProperty(value = "数值")
        private BigDecimal data;

        @ApiModelProperty(value = "得分")
        private BigDecimal score;

    }

}
