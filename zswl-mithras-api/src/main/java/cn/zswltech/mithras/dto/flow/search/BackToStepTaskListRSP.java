package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 我发起的-审批退回
 *
 * @author wangchuanhao
 * @date 2022/7/28 2:26 PM
 */
@Data
public class BackToStepTaskListRSP extends TaskListRSP {

    @ApiModelProperty("退回人id")
    private Long backUserId;

    @ApiModelProperty("退回用户名称")
    private String backUserName;

    @ApiModelProperty("退回节点id")
    private String backActivityId;

    @ApiModelProperty("退回节点名称")
    private String backNodeName;

}
