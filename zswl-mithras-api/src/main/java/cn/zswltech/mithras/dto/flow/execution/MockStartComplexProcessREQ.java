package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * mock启动流程
 *
 * @author wangchuanhao
 * @date 2022/8/3 10:14 AM
 */
@Data
public class MockStartComplexProcessREQ {

    @ApiModelProperty("审批人1审批人列表")
    @NotEmpty
    private List<String> userTask1AssigneeList;

    @ApiModelProperty("审批人2审批人列表")
    @NotEmpty
    private List<String> userTask2AssigneeList;

    @ApiModelProperty("上会审批人列表")
    @NotEmpty
    private List<String> userTaskPrepareMeetingAssigneeList;

    @ApiModelProperty("投票审批人列表")
    @NotEmpty
    private List<String> userTaskVoteAssigneeList;

    @ApiModelProperty("汇票审批人列表")
    @NotEmpty
    private List<String> userTaskCollectAssigneeList;

    @ApiModelProperty("总经理审批人列表")
    @NotEmpty
    private List<String> userTaskGeneralManagerAssigneeList;

    @ApiModelProperty("业务主键")
    @NotBlank
    private String businessKey;

    @ApiModelProperty("业务二级子模块")
    @NotBlank
    private String subModule;

    @ApiModelProperty("流程名称")
    @NotBlank
    private String processName;

}
