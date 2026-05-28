package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 流程列表
 *
 * @author wangchuanhao
 * @date 2022/6/22 10:51 PM
 */
@Data
@ApiModel("流程运行节点追踪")
public class ProcessTraceRSP {

    private List<Node> nodeList;

    @ApiModel("节点信息")
    @Data
    public static class Node {

        @ApiModelProperty("节点id")
        private String activityId;

        @ApiModelProperty("节点名称")
        private String name;
    }


}
