package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

/**
 * @author hekanglin
 */
@Data
@ApiModel("租后管理-租后检查计划详情-返回体")
public class AfterLeaseCheckPlanCommonlyDetailREQ {


    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("检查计划id")
    private Long checkPlanId;

    @ApiModelProperty("待办id")
    @NotNull(message = "待办id不能为空")
    private Long todoId;

}
