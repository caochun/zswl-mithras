package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingClientReportRSP {

    @ApiModelProperty(value = "历史评级信息")
    private RatingClientDetailLibRSP historyInfo;

    @ApiModelProperty(value = "定性指标")
    private List<RatingQualitativeRSP> qualitativeList;

    @ApiModelProperty(value = "定量指标")
    private List<RatingQuantitativeRSP> quantitativeList;

    @ApiModelProperty(value = "评级调整事项")
    private List<RatingQualitativeRSP> adjustEventList;

    @ApiModelProperty("评级结果")
    private RatingClientAbstractRSP ratingScoreRSP;

    @ApiModelProperty(value = "标的物得分")
    private List<RatingQualitativeRSP> subjectVesselScoreList;

    @ApiModelProperty(value = "客户综合得分")
    private List<RatingQualitativeRSP> custList;

}
