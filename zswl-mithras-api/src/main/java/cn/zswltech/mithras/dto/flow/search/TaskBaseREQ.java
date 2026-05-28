package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 任务相关查询基类
 *
 * @author wangchuanhao
 * @date 2022/6/22 10:59 PM
 */
@ApiModel("流程相关查询基类")
@Data
public class TaskBaseREQ {

    @ApiModelProperty("任务id")
    @NotBlank
    private String taskId;

}
