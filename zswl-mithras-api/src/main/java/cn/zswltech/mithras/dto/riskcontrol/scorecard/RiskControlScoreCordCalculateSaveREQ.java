package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName AreaSearchRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/2 4:03 下午
 * @Version 1.0
 **/
@ApiModel("保存评分信息-请求体")
@Data
public class RiskControlScoreCordCalculateSaveREQ {

    @ApiModelProperty(value = "地区")
    @NotEmpty(message = "地区不能为空")
    private String area;

    @ApiModelProperty(value = "省")
    @NotEmpty(message = "省不能为空")
    private String province;

    @ApiModelProperty(value = "市")
    @NotEmpty(message = "市不能为空")
    private String city;

    @ApiModelProperty(value = "行政级别")
    private String executiveLevel;

    @ApiModelProperty(value = "区域级别")
    private String regionalLevel;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "各指标打分详情")
    @NotEmpty(message = "打分详情不能为空")
    private List<TargetScoreBody> targetScoreBodies;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TargetScoreBody{
        /**
         * 指标id
         */
        @ApiModelProperty(value = "指标id")
        private Long targetId;


        @ApiModelProperty(value = "数值")
        private String data;

        /**
         * 得分
         */
        @ApiModelProperty(value = "得分")
        private BigDecimal score;
    }

}
