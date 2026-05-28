package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName AreaSearchRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/2 4:03 下午
 * @Version 1.0
 **/
@ApiModel("保存评分信息-返回体")
@Data
public class RiskControlScoreCordCalculateDetailRSP {

    @ApiModelProperty(value = "id")
    private Long id;

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

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "总分")
    private BigDecimal totalPoints;

    @ApiModelProperty(value = "创建时间")
    private LocalDate createTime;
    @ApiModelProperty(value = "修改时间")
    private LocalDate updateTime;

    @ApiModelProperty(value = "指标打分详情")
    private List<CalculateDetailBody> calculateDetailBodies;

    @Data
    public class CalculateDetailBody{

        @ApiModelProperty(value = "指标id")
        private Long targetId;

        @ApiModelProperty(value = "指标名称")
        private String targetName;

        /**
         * 指标权重
         */
        @ApiModelProperty("指标权重")
        private Long targetWeight;

        /**
         * 打分类型
         **/
        @ApiModelProperty("打分类型")
        private String gradeType;

        /**
         * 选项记分详情
         **/
        @ApiModelProperty(value = "选项记分")
        private RiskControlScoreCordOptionGrade optionGrade;

        ////分数信息
        @ApiModelProperty(value = "数值")
        private String data;

        /**
         * 得分
         */
        @ApiModelProperty("得分")
        private String score;

    }

}
