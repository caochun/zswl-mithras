package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingAmountReportRSP {

    @ApiModelProperty(value = "历史评级信息")
    private RatingAmountDetailLibRSP historyInfo;

    @ApiModelProperty(value = "评估基准")
    private Map<String,List<RatingEvaluateBaseRSP>> evaluateBaseList;

    @ApiModelProperty(value = "增信措施")
    private Map<String,List<RatingCreditMeasureRSP>> creditMeasureListMap;

    @ApiModelProperty(value = "限额")
    private RatingAmountQuotaRSP quota;

    @ApiModelProperty(value = "评估主体评级")
    private RatingAmountClientScoreRSP scoreRSP;


}
