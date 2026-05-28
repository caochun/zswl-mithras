package cn.zswltech.mithras.dto.flow.search;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务列表查询
 *
 * @author wangchuanhao
 * @date 2022/7/28 3:19 PM
 */
@Data
public class TaskListREQ extends PageReq {

    @ApiModelProperty("流程id")
    private String processInstanceId;

    @ApiModelProperty("流程名称")
    private String processName;

    @ApiModelProperty("流程模型类型")
    private String modelKey;

    @ApiModelProperty("流程模型类型集合")
    private List<String> modelKeyList;

    @ApiModelProperty("启动用户id")
    private Long startUserId;

    @ApiModelProperty("启动用户部门id")
    private Long startUserDeptId;

    @ApiModelProperty("流程申请时间从")
    private LocalDate processCreateTimeFrom;

    @ApiModelProperty("流程申请时间到")
    private LocalDate processCreateTimeTo;

    @ApiModelProperty("任务结束时间从")
    private LocalDate taskEndTimeFrom;

    @ApiModelProperty("任务结束时间到")
    private LocalDate taskEndTimeTo;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("附加")
    private ProcessTaskExtra extra;

    @ApiModelProperty("客户所属部门id")
    private Long belongDeptId;

}
