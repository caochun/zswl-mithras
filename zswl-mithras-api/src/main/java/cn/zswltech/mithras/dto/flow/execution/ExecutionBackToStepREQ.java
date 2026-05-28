package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 任务操作基类
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:24 PM
 */
@Data
@ApiModel("退回指定节点")
public class ExecutionBackToStepREQ extends ExecutionTaskBaseREQ {

    /**
     * 如果type 是 BACK_TO_STEP 就要填这个
     */
    @ApiModelProperty("驳回节点id")
    //@NotBlank
    private String activityId;

    /**
     * 根据产品的设计 目前返回的都是直达本节点
     */
    @ApiModelProperty("驳回类型：1逐级审批，2直达本节点")
    private Integer backType = 2;


    @NotBlank(message = "按钮类型不能为空")
    private String buttonKey;


}
