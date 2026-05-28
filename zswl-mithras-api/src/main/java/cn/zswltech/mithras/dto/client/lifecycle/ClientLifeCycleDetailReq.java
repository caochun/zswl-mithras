package cn.zswltech.mithras.dto.client.lifecycle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 14:45
 */
@ApiModel("客户全周期项目列表请求体")
@Data
public class ClientLifeCycleDetailReq {
    @ApiModelProperty("客户id")
    private Long clientId;
}
