package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 流程图节点
 *
 * @author wangchuanhao
 * @date 2022/8/26 10:52 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessPictureRSP {

    @ApiModelProperty("高亮连线")
    private List<String> highLine;

    @ApiModelProperty("高亮节点")
    private List<String> highPoint;

    @ApiModelProperty("已办节点")
    private List<String> iDo;

    @ApiModelProperty("待办节点")
    private List<String> waitingToDo;

    @ApiModelProperty("退回节点")
    private List<String> backNodeList;


}
