package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/24
 * @description
 */
@Data
public abstract class AbstractProcessInfoRSP {
    @ApiModelProperty("审批状态")
    private String processStatus;

    @ApiModelProperty("流程类型")
    private String processType;

    @ApiModelProperty("当前节点")
    private String currentNode;

    @ApiModelProperty("审批时间")
    private LocalDate approveTime;
}
