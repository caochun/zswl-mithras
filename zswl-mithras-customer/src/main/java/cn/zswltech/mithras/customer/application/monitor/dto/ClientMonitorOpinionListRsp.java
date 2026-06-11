package cn.zswltech.mithras.customer.application.monitor.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 15:20
 */
@ApiModel
@Data
public class ClientMonitorOpinionListRsp {

    private Long id;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("统一社会信用代码")
    private String uscc;
    // 标题
    private String title;

    @ApiModelProperty("预警星级")
    private Integer warnStar;

    @ApiModelProperty("预警级别")
    private Integer warnLevel;

    /**
     * 预警日期
     */
    @ApiModelProperty("预警日期")
    private LocalDateTime dataTime;
    @ApiModelProperty("状态 RiskControlOpinionHandleStatus")
    private String handleResult;
}
