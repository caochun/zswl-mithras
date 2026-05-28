package cn.zswltech.mithras.dto.flow.search;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程列表查询
 *
 * @author wangchuanhao
 * @date 2022/7/28 3:19 PM
 */
@Data
public class ProcessListREQ extends PageReq {

    @ApiModelProperty("流程id")
    private List<String> processInstanceIdList;

    @ApiModelProperty("流程id")
    private String processInstanceId;

    @ApiModelProperty("流程名称")
    private String processName;

    @ApiModelProperty("流程模型类型")
    private String modelKey;

    @ApiModelProperty("流程模型类型")
    private List<String> modelKeyList;

    @ApiModelProperty("启动用户id")
    private Long startUserId;

    @ApiModelProperty("启动用户部门id")
    private Long startUserDeptId;

    @ApiModelProperty("流程申请时间从")
    private LocalDateTime processCreateTimeFrom;

    @ApiModelProperty("流程申请时间到")
    private LocalDateTime processCreateTimeTo;

    @ApiModelProperty("审批状态")
    private String processStatus;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("业务IDs")
    private List<String> businessKeyList;

    @ApiModelProperty("附加")
    private ProcessTaskExtra extra;

}
