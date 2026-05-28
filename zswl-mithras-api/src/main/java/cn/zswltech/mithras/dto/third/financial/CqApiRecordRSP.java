package cn.zswltech.mithras.dto.third.financial;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/7/15
 * @description
 */
@Data
public class CqApiRecordRSP {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("苍穹单据类型")
    private String billType;

    @ApiModelProperty("唯一标识编号")
    private String businessId;

    @ApiModelProperty("请求参数")
    private String reqJson;

    @ApiModelProperty("返回参数")
    private String resJson;

    @ApiModelProperty("是否已操作")
    private Integer isDone;

    @ApiModelProperty("情况说明")
    private String situationDescription;

    @ApiModelProperty("关联流水类型")
    private String source;

    @ApiModelProperty("关联流水类型名称")
    private String sourceName;

    @ApiModelProperty("关联id")
    private String businessTitle;

    @ApiModelProperty("接口状态")
    private String status;

    @ApiModelProperty("失败原因")
    private String withdrawFailMessage;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
