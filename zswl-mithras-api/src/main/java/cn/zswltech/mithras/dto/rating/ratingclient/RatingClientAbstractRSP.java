package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingClientAbstractRSP {

    @ApiModelProperty(value = "定性得分")
    private String qualitativeScore;

    @ApiModelProperty(value = "定量得分")
    private String quantitativeScore;

    @ApiModelProperty(value = "主体得分")
    private String subjectScore;

    @ApiModelProperty(value = "区域得分")
    private String areaScore;

    @ApiModelProperty(value = "模型得分")
    private String modelScore;

    @ApiModelProperty(value = "违约率")
    private String defaultRate;

    @ApiModelProperty(value = "初评结果")
    private String firstScore;

    @ApiModelProperty(value = "系统评级结果")
    private String score;

    @ApiModelProperty(value = "业务调整结果")
    private String adjustScore;

    @ApiModelProperty(value = "审查结果")
    private String finalScore;

    @ApiModelProperty(value = "标的物得分")
    private String subjectVesselScore;

    @ApiModelProperty(value = "客户综合得分")
    private String custScore;

}
