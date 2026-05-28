package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/14
 * @description
 */
@Data
@ApiModel("租后管理-租后检查计划-下载指定部门的检查报告-请求体")
public class AfterLeaseCheckReportDownloadREQ {
    @ApiModelProperty("租后检查计划id")
    @NotNull(message = "租后检查计划id不能为空")
    private Long planId;

    @ApiModelProperty("业务部门id")
    @NotNull(message = "业务部门id不能为空")
    private Long deptId;

}
