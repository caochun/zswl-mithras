package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 工作台-卡片指标
 * @date 2023-05-09
 */
@Data
@ApiModel("工作台-卡片指标列表-请求体")
public class WorkbenchMetricReq {
    @ApiModelProperty(value = "指标单位")
    @NotNull(message = "当前角色编码不能为空")
    private String currentRoleCode;
}
