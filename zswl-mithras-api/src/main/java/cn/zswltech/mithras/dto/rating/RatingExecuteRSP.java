package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RatingExecuteRSP {


    @ApiModelProperty(value = "初评得分")
    private String firstScore;

    @ApiModelProperty(value = "调整后得分")
    private String score;

//    @ApiModelProperty(value = "客户限额")
//    private String clientQuota;

    @ApiModelProperty(value = "项目限额")
    private String projQuota;

    @ApiModelProperty(value = "试算次数")
    private int executeCount;

    @ApiModelProperty(value = "最大试算次数")
    private int executeCountLimit = 3;

    @ApiModelProperty(value = "评分卡版本")
    private String scoreCardVersion;

    @ApiModelProperty(value = "评分卡名称")
    private String scoreCardName;

    @ApiModelProperty(value = "评分卡编号")
    private String scoreCardCode;

}
