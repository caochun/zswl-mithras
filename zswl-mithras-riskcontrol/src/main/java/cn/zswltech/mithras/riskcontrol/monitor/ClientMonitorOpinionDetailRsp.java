package cn.zswltech.mithras.riskcontrol.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 16:00
 */
@ApiModel
@Data
public class ClientMonitorOpinionDetailRsp {

    @ApiModelProperty("舆情日期")
    private LocalDateTime date;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("预警星级")
    private Integer warnStar;

    @ApiModelProperty("预警级别")
    private Integer warnLevel;

    @ApiModelProperty("舆情状态")
    private String opinionStatus;

    // 标题
    private String title;
    // 链接
    private String linkAddress;
}
