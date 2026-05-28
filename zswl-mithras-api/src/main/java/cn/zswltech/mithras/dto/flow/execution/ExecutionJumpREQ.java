package cn.zswltech.mithras.dto.flow.execution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 节点跳转
 *
 * @author wangchuanhao
 * @date 2022/7/28 3:06 PM
 */
@Data
public class ExecutionJumpREQ extends ExecutionProcessBaseREQ {

    @ApiModelProperty("跳转至哪个节点id")
    private String activityId;

}
