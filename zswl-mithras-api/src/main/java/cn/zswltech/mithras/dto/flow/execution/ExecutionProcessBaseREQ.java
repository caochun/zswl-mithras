package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 流程操作基类
 *
 * @author wangchuanhao
 * @date 2022/7/28 2:47 PM
 */
@Data
@ApiModel("流程操作基类")
public class ExecutionProcessBaseREQ {

    @ApiModelProperty("流程id")
    private String processInstanceId;

    @ApiModelProperty("操作意见")
    private String message;

    @ApiModelProperty("抄送用户id列表")
    private List<Long> ccUserIdList;

    @ApiModelProperty("更新数据场景，0/null页面列表，1审批详情")
    private Integer scene;
}
