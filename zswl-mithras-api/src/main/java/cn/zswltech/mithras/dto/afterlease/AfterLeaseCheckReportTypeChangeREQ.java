package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@Data
@ApiModel("租后检查项目-更换报告类型（变更模板）-请求体")
public class AfterLeaseCheckReportTypeChangeREQ {
    @NotNull(message = "检查计划项目id不能为空")
    @ApiModelProperty("主键id")
    private Long id;

    @NotBlank(message = "报告类型不能为空")
    @ApiModelProperty("报告模板类型")
    private String reportType;
}
