package cn.zswltech.mithras.blackgray.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class BlackGrayWarehouseApprovalTaskRSP extends BlackGrayLibraryListRSP {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date updateTime;

    @ApiModelProperty(value = "创建人")
    private String createdBy;

    @ApiModelProperty(value = "修改人")
    private String updatedBy;

    /**
     * 审批任务id
     */
    private Long auditTaskId;

    /**
     * 审批状态
     * @see cn.zswltech.mithras.blackgray.enums.AuditStatusEnum
     */
    @ApiModelProperty(value = "审批状态[0:待提交,1:审批中,2:已撤回,3:已驳回,4:已完成]")
    private Integer auditStatus;

    @ApiModelProperty("上一处理人")
    private String preOperator;

    @ApiModelProperty("当前审批人")
    private String currentOperator;


    private String latestMsg;

}
