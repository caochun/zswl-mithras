package cn.zswltech.mithras.service.providence.dto;

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
public class ClientMonitorListReq extends PageReq {
    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("只看预警, 1表示true")
    private Integer onlyWarn;
}
