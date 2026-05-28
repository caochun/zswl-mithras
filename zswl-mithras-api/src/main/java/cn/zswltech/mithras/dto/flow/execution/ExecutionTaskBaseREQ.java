package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 任务操作基类
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:24 PM
 */
@Data
@ApiModel("任务操作基类")
public class ExecutionTaskBaseREQ {

    @ApiModelProperty("任务id")
    @NotBlank
    private String taskId;

    @ApiModelProperty("操作意见")
    private String message;

    @ApiModelProperty("抄送用户id列表")
    private List<Long> ccUserIdList;

    @ApiModelProperty("是否关注流程，1关注0不关注")
    private Integer attentionFlag;


}
