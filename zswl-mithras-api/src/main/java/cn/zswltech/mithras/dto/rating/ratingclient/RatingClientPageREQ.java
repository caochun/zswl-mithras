package cn.zswltech.mithras.dto.rating.ratingclient;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RatingClientPageREQ extends PageReq {

    @ApiModelProperty(value = "客户id，展示tab页数据时需要传递")
    private Long clientId;

    @ApiModelProperty(value = "客户编号")
    private String clientCode;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "发起时间-开始")
    private LocalDate createTimeFrom;

    @ApiModelProperty(value = "发起时间-结束")
    private LocalDate createTimeTo;

    @ApiModelProperty(value = "评级状态")
    private Boolean ratingStatus;

    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    @ApiModelProperty(value = "评级类型")
    private String ratingType;

}
