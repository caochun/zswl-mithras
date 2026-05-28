package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2023/10/11/11:10
 * @description
 */
@Data
public class AfterLeaseAuditFlowPreREQ {

    @ApiModelProperty(value = "租后检查报告id")
    private Long planClientId;

    @Deprecated
    @ApiModelProperty(value = "客户ID")
    //@NotNull(message = "客户ID不能为空")
    private Long clientId;

    @ApiModelProperty(value = "待办id")
    private Long todoId;
}
