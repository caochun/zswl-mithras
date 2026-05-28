package cn.zswltech.mithras.dto.client.client;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 生效
 *
 * @author zhouning
 * @date 2024/10/14 11:56 PM
 */
@Data
@ApiModel("融租易APP客户项下未审批完成的租后检查计划-请求体")
public class ClientAppPlanQueryREQ extends PageReq {

    @ApiModelProperty("客户Id")
    private Long clientId;

}
