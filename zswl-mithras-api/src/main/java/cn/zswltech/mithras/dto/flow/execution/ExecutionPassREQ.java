package cn.zswltech.mithras.dto.flow.execution;

import cn.zswltech.mithras.dto.process.modify.remark.ProcessModifyRemarkAddREQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过
 *
 * @author wangchuanhao
 * @date 2022/8/2 11:27 PM
 */
@Data
public class ExecutionPassREQ extends ExecutionTaskBaseREQ {

    @NotBlank(message = "按钮类型不能为空")
    private String buttonKey;

    @ApiModelProperty("动态表单数据")
    private Map<String, Object> dynamicFormData = new HashMap<>();

    @ApiModelProperty("变更说明")
    private ProcessModifyRemarkAddREQ remarkAddREQ;

}
