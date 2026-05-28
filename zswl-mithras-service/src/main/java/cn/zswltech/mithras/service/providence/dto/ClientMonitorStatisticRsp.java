package cn.zswltech.mithras.service.providence.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 15:34
 */
@ApiModel
@Data
public class ClientMonitorStatisticRsp {

    /**
     * 监控客户数
     */
    @ApiModelProperty("监控客户数")
    private Integer monitorClientCount;
    /**
     * 预警客户数
     */
    @ApiModelProperty("预警客户数")
    private Integer warnClientCount;
    /**
     * 今日新增预警客户数
     */
    @ApiModelProperty("今日新增预警客户数")
    private Integer newWarnClientCount;
    /**
     * 今日预警全关闭客户数
     */
    @ApiModelProperty("今日预警全关闭客户数")
    private Integer closeWarnClientCount;
    /**
     * 舆情客户数
     */
    @ApiModelProperty("舆情客户数")
    private Integer opClientCount;
    /**
     * 今日新增舆情客户数
     */
    @ApiModelProperty("今日新增舆情")
    private Integer newOpClientCount;
    /**
     * 今日舆情全关闭客户数
     */
    @ApiModelProperty("今日舆情全关闭客户数")
    private Integer closeOpClientCount;


}
