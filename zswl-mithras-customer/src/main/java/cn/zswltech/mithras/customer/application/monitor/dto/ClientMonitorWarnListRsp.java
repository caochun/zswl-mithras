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
public class ClientMonitorWarnListRsp {

    private Long id;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("标题")
    private String warnTitle;
    @ApiModelProperty("编号")
    private String warnCode;
    @ApiModelProperty("状态 RiskControlOpinionHandleStatus")
    private String warnStatus;
    @ApiModelProperty("统一社会信用代码")
    private String uscc;

    /**
     * 预警日期
     */
    @ApiModelProperty("预警日期")
    private LocalDateTime dataTime;

    /**
     * 预警信号：1：绿灯；2：黄灯；3：红灯
     */
    @ApiModelProperty("预警信号：1：绿灯；2：黄灯；3：红灯")
    private Integer warnLevel;

}
