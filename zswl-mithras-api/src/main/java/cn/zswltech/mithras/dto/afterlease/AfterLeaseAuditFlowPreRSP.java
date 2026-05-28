package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 * @date 2023/10/11/11:06
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AfterLeaseAuditFlowPreRSP {

    @ApiModelProperty(value = "审批快照实例ID")
    private String processInstanceId;

    @ApiModelProperty(value = "审批快照按钮展示标志, 0: 不展示 1: 展示")
    private Integer auditButtonOpenFlag;
}
