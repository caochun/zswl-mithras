package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/7/1/10:35
 * @description
 */
@Data
public class KpiPerformanceManageModifyREQ {

    @ApiModelProperty(value = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @ApiModelProperty(value = "是否启用")
    @NotNull(message = "是否启用信息不能为空")
    private Integer status;
}
