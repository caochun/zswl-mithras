package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * mock启动流程
 *
 * @author wangchuanhao
 * @date 2022/8/3 10:14 AM
 */
@Data
public class MockStartProcessREQ {

    @ApiModelProperty("用户节点审批人{节点id:用户id列表}")
    @NotNull
    private Map<String, List<Long>> assigneeListMap;

    @ApiModelProperty("业务主键")
    @NotNull
    private Long businessKey;

    @ApiModelProperty("业务二级子模块")
    @NotBlank
    private String subModule;

    @ApiModelProperty("流程名称")
    @NotBlank
    private String processName;

}
