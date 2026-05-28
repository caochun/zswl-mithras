package cn.zswltech.mithras.dto.client.lifecycle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 14:29
 */
@ApiModel("客户全周期卡片返回")
@Data
public class ClientLifeCycleCardRsp {

    @ApiModelProperty("总客户数")
    private Integer totalClientNum;

    @ApiModelProperty("总数本月新增")
    private Integer totalClientNumMonthIncrease;

    @ApiModelProperty("存续客户数")
    private Integer existingClientNum;

    @ApiModelProperty("存续客户数本月新增")
    private Integer existingClientNumMonthIncrease;

    @ApiModelProperty("结清客户数")
    private Integer settledClientNum;

    @ApiModelProperty("结清客户数本月新增")
    private Integer settledClientNumMonthIncrease;

    @ApiModelProperty("逾期客户数")
    private Integer overdueClientNum;

    @ApiModelProperty("逾期客户数本月新增")
    private Integer overdueClientNumMonthIncrease;

}
