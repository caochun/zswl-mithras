package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingClientOverturnRecordRSP {

    @ApiModelProperty(value = "推翻时间")
    private LocalDateTime overturnTime;

    @ApiModelProperty(value = "评级结果")
    private String score;

    @ApiModelProperty(value = "评级认定结果")
    private String finalScore;

    @ApiModelProperty(value = "调整类型")
    private String adjustType;

    @ApiModelProperty(value = "推翻理由")
    private String overturnOpinion;

    @ApiModelProperty(value = "推翻人")
    private String overturnUserName;

}
