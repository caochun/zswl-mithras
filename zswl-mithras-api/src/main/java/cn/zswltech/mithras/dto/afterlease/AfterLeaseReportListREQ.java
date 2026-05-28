package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Data
@ApiModel("租后调整报告清单列表-请求体")
public class AfterLeaseReportListREQ {
    @NotNull(message = "租后调整记录id不能为空")
    @ApiModelProperty("租后调整记录id")
    private Long adjustId;

    @ApiModelProperty("所处流程实例id")
    private String processInstanceId;
}
