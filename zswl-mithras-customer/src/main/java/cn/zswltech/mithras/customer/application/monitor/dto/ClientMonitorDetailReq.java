package cn.zswltech.mithras.customer.application.monitor.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 15:44
 */
@ApiModel()
@Data
public class ClientMonitorDetailReq {
    @ApiModelProperty("客户id")
    private Long clientId;
}
