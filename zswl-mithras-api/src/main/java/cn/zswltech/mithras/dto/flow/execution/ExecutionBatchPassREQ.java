package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/1/18
 * @description
 */
@Data
@ApiModel("一键通过")
public class ExecutionBatchPassREQ {
    @ApiModelProperty("审批任务id")
    @NotEmpty(message = "审批任务id不能为空")
    private List<String> taskIdList;
}
