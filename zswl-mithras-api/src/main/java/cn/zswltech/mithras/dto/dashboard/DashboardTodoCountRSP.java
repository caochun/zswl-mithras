package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 工作台栏目的数量
 *
 * @author zhouning
 * @date 2024/06/20 5:36 PM
 */
@Data
public class DashboardTodoCountRSP {

    @ApiModelProperty("待办数量")
    private Integer todoCount;

    @ApiModelProperty("我发起的数量")
    private Integer myInitiateCount;

    @ApiModelProperty("在办数量")
    private Integer doingCount;

    @ApiModelProperty("已办数量")
    private Integer doneCount;

    @ApiModelProperty("流程抄送数量")
    private Integer ccCount;
}
