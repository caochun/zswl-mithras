package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务操作基类
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:24 PM
 */
@Data
@ApiModel("退回到发起人")
public class ExecutionBackToStartUserREQ extends ExecutionTaskBaseREQ {

    @ApiModelProperty("驳回类型：1逐级审批，2直达本节点")
    private Integer backType;

//    @NotBlank(message = "按钮类型不能为空")
    @ApiModelProperty("按钮类型")
    private String buttonKey;

    @ApiModelProperty("动态表单数据")
    private Map<String, Object> dynamicFormData = new HashMap<>();
}
