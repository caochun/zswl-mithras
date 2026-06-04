package cn.zswltech.mithras.customer.interfaces.providence.dto;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 15:25
 */
@ApiModel()
@Data
public class ClientMonitorOpinionListReq extends PageReq {
    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("舆情状态")
    private String opinionStatus;
}
