package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 生效
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Data
@ApiModel("融租易APP客户项下未审批完成的租后检查计划-返回体")
public class ClientAppPlanQueryRSP {

    @ApiModelProperty(value = "租后检查计划名称")
    private String checkPlanName;

    @ApiModelProperty(value = "租后检查计划Id")
    private Long checkPlanId;

}
