package cn.zswltech.mithras.riskcontrol.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 15:58
 */
@ApiModel
@Data
public class ClientMonitorWarnDetailRsp {
    @ApiModelProperty("时间")
    private LocalDateTime date;
    @ApiModelProperty("标题")
    private String warnTitle;
    @ApiModelProperty("编号")
    private String warnCode;
    @ApiModelProperty("等级")
    private String warnLevel;
    @ApiModelProperty("状态")
    private String warnStatus;
}
