package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RatingClientPageRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "客户编号")
    private String clientCode;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "模型编码")
    private String modelCode;

    @ApiModelProperty(value = "评级结果")
    private String score;

    @ApiModelProperty(value = "评级认定结果")
    private String finalScore;

    @ApiModelProperty(value = "发起时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "生效日期")
    private LocalDate effectTime;

    @ApiModelProperty(value = "失效日期")
    private LocalDate abandonTime;

    @ApiModelProperty(value = "发起机构")
    private String startOrg;

    @ApiModelProperty(value = "发起人")
    private Long createBy;

    @ApiModelProperty(value = "发起人名称")
    private String createByName;

    @ApiModelProperty(value = "评级状态")
    private String ratingStatus;

    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    @ApiModelProperty(value = "评级类型")
    private String ratingType;

}
