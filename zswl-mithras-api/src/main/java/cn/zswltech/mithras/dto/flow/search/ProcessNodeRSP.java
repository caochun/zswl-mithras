package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程节点
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:20 PM
 */
@ApiModel("流程节点返回值")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessNodeRSP {

    @ApiModelProperty("节点id")
    private String activityId;

    @ApiModelProperty("节点名称")
    private String name;

}
